/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.pay.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wit.payment.domain.pay.dto.request.CartItemRequest;
import com.wit.payment.domain.pay.dto.request.DeliverySaveRequest;
import com.wit.payment.domain.pay.dto.request.PayRequest;
import com.wit.payment.domain.pay.dto.request.PaymentItemOptionRequest;
import com.wit.payment.domain.pay.dto.request.PaymentOptionAndDeliveryRequest;
import com.wit.payment.domain.pay.dto.response.PayResponse;
import com.wit.payment.domain.pay.dto.response.PaymentIssueResponse;
import com.wit.payment.domain.pay.dto.response.PaymentSummaryResponse;
import com.wit.payment.domain.pay.dto.response.PaymentWithItemsResponse;
import com.wit.payment.domain.pay.entity.Payment;
import com.wit.payment.domain.pay.entity.PaymentDelivery;
import com.wit.payment.domain.pay.entity.PaymentIssue;
import com.wit.payment.domain.pay.entity.PaymentItem;
import com.wit.payment.domain.pay.exception.PaymentErrorCode;
import com.wit.payment.domain.pay.mapper.PaymentMapper;
import com.wit.payment.domain.pay.repository.PaymentDeliveryRepository;
import com.wit.payment.domain.pay.repository.PaymentIssueRepository;
import com.wit.payment.domain.pay.repository.PaymentRepository;
import com.wit.payment.domain.product.entity.Product;
import com.wit.payment.domain.product.repository.ProductRepository;
import com.wit.payment.global.exception.CustomException;
import com.wit.payment.global.tl3800.TL3800Gateway;
import com.wit.payment.global.tl3800.proto.TLPacket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PayService {

  private final ProductRepository productRepository;
  private final PaymentRepository paymentRepository;
  private final PaymentDeliveryRepository paymentDeliveryRepository;
  private final PaymentIssueRepository paymentIssueRepository;
  private final TL3800Gateway tl3800Gateway;
  private final PaymentMapper paymentMapper;

  /**
   * 결제 요청 처리
   *
   * <p>1) 상품/금액 검증 2) 단말 승인 요청 3) 승인 성공 시 Payment + PaymentItem 저장 4) 거절/오류 시 PaymentIssue 저장
   */
  @Transactional
  public PayResponse pay(PayRequest request) {
    log.info(
        "[PAY] 결제 요청 수신 - items={}, totalAmount={}, inst={}, delivery={}",
        request.items(),
        request.totalAmount(),
        request.inst(),
        request.delivery());

    // 1. 상품 및 금액 검증
    long serverTotal = calculateTotalAmount(request.items());
    log.info("[PAY] 서버 계산 금액={} / 프론트 전송 금액={}", serverTotal, request.totalAmount());

    if (!serverTotalEqualsRequest(serverTotal, request.totalAmount())) {
      log.warn(
          "[PAY] 금액 불일치 - 서버 계산 금액={}, 요청 금액={}, items={}",
          serverTotal,
          request.totalAmount(),
          request.items());
      throw new CustomException(PaymentErrorCode.AMOUNT_MISMATCH);
    }

    // TL3800 은 문자열 금액 사용
    String amountStr = String.valueOf(serverTotal);

    try {
      // 2. 단말 승인 요청
      log.info("[PAY] TL3800 승인 요청 시작 - amount={}, inst={}", amountStr, request.inst());

      TLPacket resp = tl3800Gateway.approve(amountStr, "0", "0", request.inst(), true);
      int respCode = Byte.toUnsignedInt(resp.responseCode);

      log.info("[PAY] TL3800 승인 응답 수신 - jobCode={}, responseCode={}", resp.jobCode, respCode);

      // 3. 응답 코드 기준 성공/실패 분기
      if (respCode == 0) {
        // 3-1. 결제 엔티티 생성
        Payment payment =
            paymentMapper.toPayment(
                resp,
                serverTotal,
                request.inst(),
                request.delivery(),
                request.phoneNumber(),
                request.imageUrl());

        // 3-2. 상품별 PaymentItem 생성 (각 productId당 1개, 옵션은 나중에 별도 API로 설정)
        populatePaymentItems(payment, request.items());

        // 3-3. Payment + PaymentItem 저장 (cascade = ALL)
        Payment saved = paymentRepository.save(payment);

        log.info("[PAY] 결제 성공 - paymentId={}, approvalNo={}", saved.getId(), saved.getApprovalNo());

        return new PayResponse(true, saved.getId(), null, "결제가 완료되었습니다.");
      }

      // responseCode != 0 → 단말 거절
      String issueMessage = "[단말 거절] 응답코드=" + respCode;
      PaymentIssue issue = paymentMapper.toIssue(serverTotal, issueMessage, request.phoneNumber());
      PaymentIssue savedIssue = paymentIssueRepository.save(issue);

      log.warn("[PAY] 단말 거절 - issueId={}, respCode={}", savedIssue.getId(), respCode);

      // 여기서는 예외를 던지지 않고, 실패 응답으로 내려주는 형태
      return new PayResponse(false, null, savedIssue.getId(), savedIssue.getMessage());

    } catch (CustomException e) {
      throw e;
    } catch (Exception ex) {
      // 4. 타임아웃 / 통신 예외 등
      log.error("[PAY] 단말/결제 처리 중 예외 발생", ex);

      String issueMessage = "[예외] " + ex.getClass().getSimpleName() + ": " + ex.getMessage();
      long safeAmount = Math.min(serverTotal, Integer.MAX_VALUE);

      PaymentIssue issue = paymentMapper.toIssue(safeAmount, issueMessage, request.phoneNumber());
      PaymentIssue savedIssue = paymentIssueRepository.save(issue);

      log.warn(
          "[PAY] 예외로 인해 PaymentIssue 생성 - issueId={}, message={}",
          savedIssue.getId(),
          savedIssue.getMessage());

      throw new CustomException(PaymentErrorCode.TERMINAL_ERROR);
    }
  }

  /** [조회] 결제 내역 전체 */
  public List<PaymentSummaryResponse> getAllPayments() {
    List<Payment> payments = paymentRepository.findAllByOrderByApprovedDateDescApprovedTimeDesc();

    log.info("[PAY] 결제 내역 전체 조회 - count={}", payments.size());

    return paymentMapper.toPaymentResponseList(payments);
  }

  /** [조회] 결제 이슈 내역 전체 */
  public List<PaymentIssueResponse> getAllPaymentIssues() {
    List<PaymentIssue> issues =
        paymentIssueRepository.findAllByOrderByOccurredDateDescOccurredTimeDesc();

    log.info("[PAY] 결제 이슈 내역 전체 조회 - count={}", issues.size());

    return paymentMapper.toIssueResponseList(issues);
  }

  /** 요청된 상품/수량을 기준으로 총 결제 금액 계산 및 검증 */
  private long calculateTotalAmount(List<CartItemRequest> items) {
    if (items == null || items.isEmpty()) {
      log.warn("[PAY] 빈 상품 목록으로 결제 요청");
      throw new CustomException(PaymentErrorCode.EMPTY_ITEMS);
    }

    // 1) 상품 ID 목록
    List<Long> productIds = items.stream().map(CartItemRequest::productId).distinct().toList();

    // 2) 상품 조회
    List<Product> products = productRepository.findByIdIn(productIds);
    Map<Long, Product> productMap =
        products.stream().collect(Collectors.toMap(Product::getId, p -> p));

    // 3) 모든 상품이 존재하는지 검증
    if (products.size() != productIds.size()) {
      log.warn("[PAY] 존재하지 않는 상품 ID 포함 - 요청ID={}, 실제존재수={}", productIds, products.size());
      throw new CustomException(PaymentErrorCode.PRODUCT_NOT_FOUND);
    }

    // 4) 각 상품 상태/가격 검증 + 합산
    long total = 0L;
    for (CartItemRequest item : items) {
      Product p = productMap.get(item.productId());

      // 상품 상태 검증 (예: 판매중이 아닌 경우)
      if (!p.isOrderable()) {
        log.warn("[PAY] 주문 불가 상품 상태 - productId={}, status={}", p.getId(), p.getStatus());
        throw new CustomException(PaymentErrorCode.PRODUCT_STATUS_INVALID);
      }

      long unitPrice = p.getPrice();
      long lineAmount = unitPrice * item.quantity();
      total += lineAmount;

      log.debug(
          "[PAY] 상품 금액 계산 - productId={}, unitPrice={}, quantity={}, lineAmount={}",
          p.getId(),
          unitPrice,
          item.quantity(),
          lineAmount);
    }

    log.info("[PAY] 상품 금액 합계 계산 완료 - total={}", total);
    return total;
  }

  /** 결제 성공 시 PaymentItem 생성 (상품별 1개씩, 옵션은 이후 API로 설정) */
  private void populatePaymentItems(Payment payment, List<CartItemRequest> items) {
    if (items == null || items.isEmpty()) {
      return;
    }

    List<Long> distinctProductIds =
        items.stream().map(CartItemRequest::productId).distinct().toList();

    for (Long productId : distinctProductIds) {
      PaymentItem item = PaymentItem.builder().productId(productId).optionText(null).build();
      payment.addItem(item);
    }
  }

  /** 배송 정보 저장/수정 */
  @Transactional
  public void saveDelivery(Long paymentId, DeliverySaveRequest request) {
    Payment payment =
        paymentRepository
            .findById(paymentId)
            .orElseThrow(() -> new CustomException(PaymentErrorCode.PAYMENT_NOT_FOUND));

    PaymentDelivery delivery =
        paymentDeliveryRepository
            .findByPayment(payment)
            .map(
                existing -> {
                  existing.update(
                      request.name(),
                      request.zipCode(),
                      request.address(),
                      request.detailAddress());
                  return existing;
                })
            .orElseGet(
                () -> {
                  PaymentDelivery newDelivery =
                      PaymentDelivery.builder()
                          .payment(payment)
                          .name(request.name())
                          .zipCode(request.zipCode())
                          .address(request.address())
                          .detailAddress(request.detailAddress())
                          .build();
                  payment.setDelivery(newDelivery);
                  return newDelivery;
                });

    paymentDeliveryRepository.save(delivery);

    log.info(
        "[PAY] 배송 정보 저장 완료 - paymentId={}, recipient={}, zipCode={}",
        paymentId,
        request.name(),
        request.zipCode());
  }

  /** 서버 계산 금액과 프론트에서 전달한 totalAmount 비교 */
  private boolean serverTotalEqualsRequest(long serverTotal, Long requestTotal) {
    if (requestTotal == null) {
      return false;
    }
    return serverTotal == requestTotal;
  }

  /**
   * 결제 상품 옵션(여러 개) + 배송지 정보를 한 번에 저장
   *
   * <p>- paymentId로 Payment 조회
   *
   * <p>- 요청에 포함된 각 productId에 대해 PaymentItem.optionText 갱신
   *
   * <p>- delivery 정보가 존재하면 배송 엔티티 및 Payment.deliveryAddress 갱신
   */
  @Transactional
  public void saveOptionsAndDelivery(PaymentOptionAndDeliveryRequest request) {

    // 1. 전화번호 기준 가장 최근 결제 조회
    Payment payment =
        paymentRepository
            .findTopByPhoneNumberOrderByApprovedDateDescApprovedTimeDesc(request.phoneNumber())
            .orElseThrow(() -> new CustomException(PaymentErrorCode.PAYMENT_NOT_FOUND));

    Long paymentId = payment.getId();

    // 2. 옵션 저장
    if (request.items() != null && !request.items().isEmpty()) {
      Map<Long, PaymentItem> itemByProductId =
          payment.getItems().stream().collect(Collectors.toMap(PaymentItem::getProductId, i -> i));

      for (PaymentItemOptionRequest optionReq : request.items()) {
        PaymentItem item = itemByProductId.get(optionReq.productId());
        if (item == null) {
          throw new CustomException(PaymentErrorCode.PAYMENT_ITEM_NOT_FOUND);
        }
        item.updateOptionText(optionReq.optionText());

        log.info(
            "[PAY] 결제 상품 옵션 저장 - paymentId={}, productId={}, option={}",
            paymentId,
            optionReq.productId(),
            optionReq.optionText());
      }
    }

    // 3. 배송 정보 저장/수정
    DeliverySaveRequest deliveryReq = request.delivery();
    if (deliveryReq != null) {

      PaymentDelivery delivery =
          paymentDeliveryRepository
              .findByPayment(payment)
              .map(
                  existing -> {
                    existing.update(
                        deliveryReq.name(),
                        deliveryReq.zipCode(),
                        deliveryReq.address(),
                        deliveryReq.detailAddress());
                    return existing;
                  })
              .orElseGet(
                  () -> {
                    PaymentDelivery newDelivery =
                        PaymentDelivery.builder()
                            .payment(payment)
                            .name(deliveryReq.name())
                            .zipCode(deliveryReq.zipCode())
                            .address(deliveryReq.address())
                            .detailAddress(deliveryReq.detailAddress())
                            .build();
                    payment.setDelivery(newDelivery);
                    return newDelivery;
                  });

      paymentDeliveryRepository.save(delivery);

      log.info(
          "[PAY] 배송 정보 저장 완료 - paymentId={}, recipient={}, zipCode={}",
          paymentId,
          deliveryReq.name(),
          deliveryReq.zipCode());
    } else {
      log.info(
          "[PAY] 옵션만 저장 - paymentId={}, optionCount={}",
          paymentId,
          request.items() != null ? request.items().size() : 0);
    }
  }

  @Transactional(readOnly = true)
  public List<PaymentWithItemsResponse> getPaymentsByPhone(String phoneNumber) {

    List<Payment> payments =
        paymentRepository.findByPhoneNumberOrderByApprovedDateDescApprovedTimeDesc(phoneNumber);

    log.info("[PAY] 전화번호={} 기준 결제 {}건 조회", phoneNumber, payments.size());

    return paymentMapper.toPaymentWithItemsResponseList(payments);
  }
}
