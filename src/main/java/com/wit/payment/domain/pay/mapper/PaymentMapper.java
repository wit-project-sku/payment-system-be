/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.pay.mapper;

import static java.nio.charset.StandardCharsets.US_ASCII;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Component;

import com.wit.payment.domain.pay.dto.request.PayRequest;
import com.wit.payment.domain.pay.dto.request.PaySuccessReportRequest;
import com.wit.payment.domain.pay.dto.response.PaymentIssueResponse;
import com.wit.payment.domain.pay.dto.response.PaymentItemSummaryResponse;
import com.wit.payment.domain.pay.dto.response.PaymentSummaryResponse;
import com.wit.payment.domain.pay.dto.response.PaymentWithItemsResponse;
import com.wit.payment.domain.pay.entity.Payment;
import com.wit.payment.domain.pay.entity.PaymentIssue;
import com.wit.payment.domain.pay.entity.PaymentIssueStatus;
import com.wit.payment.domain.pay.entity.PaymentItem;
import com.wit.payment.domain.product.entity.ProductImage;
import com.wit.payment.domain.product.repository.ProductRepository;
import com.wit.payment.global.tl3800.parser.TL3800ApprovalInfo;
import com.wit.payment.global.tl3800.proto.TLPacket;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentMapper {

  private final ProductRepository productRepository;

  private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
  private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HHmmss");

  /**
   * [레거시] 중앙 서버에서 TL3800을 직접 붙여 쓰던 시절의 매핑용.
   *
   * <p>이제는 로컬 에이전트 기반 구조에서 사용하지 않는 방향이지만, 기존 코드 호환을 위해 남겨 두고, 새 필드들도 최대한 채워준다.
   */
  @Deprecated
  public Payment toPayment(
      TLPacket packet,
      long amount,
      String inst,
      boolean delivery,
      String phoneNumber,
      String imageUrl) {

    // 가능하면 TL3800ApprovalInfo를 활용해서 필드를 채운다.
    TL3800ApprovalInfo info = TL3800ApprovalInfo.fromPacket(packet);

    // 승인/매출일시: TL 응답 기준
    LocalDate orgApprovedDate = info.approvedDate();
    LocalTime orgApprovedTime = info.approvedTime();

    // 금액: 인자로 받은 amount와 TL 금액이 다를 수 있으나,
    // 실제 승인 기준인 TL 금액(info.approvedAmount())를 우선 사용
    int approvedAmount = info.approvedAmount();
    int vatAmount = info.vatAmount();
    int svcAmount = info.svcAmount();

    return Payment.builder()
        // --- 우리 도메인 정보 ---
        .phoneNumber(phoneNumber)
        .imageUrl(imageUrl)
        .delivery(delivery)
        .amount(approvedAmount)
        .vatAmount(vatAmount)
        .svcAmount(svcAmount)
        .installment(inst != null ? inst : info.installment())
        // --- 승인/매출 시각(원거래 일자/시간) ---
        .orgApprovedDate(orgApprovedDate)
        .orgApprovedTime(orgApprovedTime)
        // --- 승인번호 ---
        .approvalNoRaw(info.approvalNoRaw())
        .approvalNo(info.approvalNo())
        // --- VAN 거래 식별 정보 ---
        .vanTransactionNo(info.vanTransactionId())
        .terminalNo(info.terminalNo())
        .terminalId(info.terminalId())
        .terminalSequenceNo(info.terminalSeqNo())
        // --- 거래 속성 ---
        .tranTypeCode(info.tranTypeCode())
        .mediaType(info.mediaType())
        // --- 부가정보 ---
        .vanExtraRaw(info.vanExtraRaw())
        .build();
  }

  /** 장애/예외 상황 → PaymentIssue 엔티티 변환 (전화번호 포함) */
  public PaymentIssue toIssue(long amount, String message, String phoneNumber) {
    return PaymentIssue.builder()
        .occurredDate(LocalDate.now())
        .occurredTime(LocalTime.now())
        .amount((int) amount)
        .phoneNumber(phoneNumber)
        .message(message)
        .status(PaymentIssueStatus.UNRESOLVED)
        .build();
  }

  /**
   * Payment -> 결제 내역 요약 응답 DTO
   *
   * <p>승인일자/시각은 Payment의 orgApprovedDate/orgApprovedTime을 그대로 사용 (원거래 매출일자/시간이 곧 “승인일자/승인시각”이기 때문)
   */
  public PaymentSummaryResponse toPaymentResponse(Payment payment) {
    return PaymentSummaryResponse.builder()
        .paymentId(payment.getId())
        .approvedDate(payment.getOrgApprovedDate())
        .approvedTime(payment.getOrgApprovedTime())
        .approvalNo(payment.getApprovalNo())
        .amount(payment.getAmount())
        .installment(payment.getInstallment())
        .phoneNumber(payment.getPhoneNumber())
        .build();
  }

  public PaymentIssueResponse toIssueResponse(PaymentIssue issue) {
    return PaymentIssueResponse.builder()
        .paymentIssueId(issue.getId())
        .occurredDate(issue.getOccurredDate())
        .occurredTime(issue.getOccurredTime())
        .amount(issue.getAmount())
        .message(issue.getMessage())
        .status(issue.getStatus())
        .build();
  }

  public List<PaymentSummaryResponse> toPaymentResponseList(List<Payment> payments) {
    if (payments == null) {
      return List.of();
    }
    return payments.stream().map(this::toPaymentResponse).toList();
  }

  public List<PaymentIssueResponse> toIssueResponseList(List<PaymentIssue> issues) {
    if (issues == null) {
      return List.of();
    }
    return issues.stream().map(this::toIssueResponse).toList();
  }

  /** PaymentItem -> DTO */
  public PaymentItemSummaryResponse toPaymentItemSummaryResponse(PaymentItem item) {
    String imageUrl =
        productRepository
            .findById(item.getProductId())
            .flatMap(
                product ->
                    product.getImages().stream()
                        .sorted(Comparator.comparingInt(ProductImage::getOrderNum))
                        .map(ProductImage::getImageUrl)
                        .findFirst())
            .orElse(null);

    return PaymentItemSummaryResponse.builder()
        .productId(item.getProductId())
        .imageUrl(imageUrl)
        .optionText(item.getOptionText())
        .build();
  }

  /** Payment + Items -> Response DTO */
  public PaymentWithItemsResponse toPaymentWithItemsResponse(Payment payment) {

    List<PaymentItemSummaryResponse> itemResponses =
        payment.getItems().stream().map(this::toPaymentItemSummaryResponse).toList();

    String deliveryAddress = null;
    if (payment.getDeliveryDetail() != null) {
      deliveryAddress =
          payment.getDeliveryDetail().getAddress()
              + " "
              + payment.getDeliveryDetail().getDetailAddress();
    }

    return PaymentWithItemsResponse.builder()
        .paymentId(payment.getId())
        .deliveryAddress(deliveryAddress)
        .items(itemResponses)
        .build();
  }

  public List<PaymentWithItemsResponse> toPaymentWithItemsResponseList(List<Payment> payments) {
    return payments.stream().map(this::toPaymentWithItemsResponse).toList();
  }

  /**
   * 로컬 에이전트 → 중앙 서버 성공 보고 DTO + TL 파싱 결과 → Payment 엔티티 변환
   *
   * <p>이 메서드가 이제 "정식" 경로.
   */
  public Payment toPayment(PaySuccessReportRequest report, TL3800ApprovalInfo info) {

    PayRequest req = report.payRequest();

    Payment.PaymentBuilder builder =
        Payment.builder()
            // --- 우리 도메인 정보 ---
            .phoneNumber(req.phoneNumber())
            .imageUrl(req.imageUrl())
            .delivery(req.delivery())
            // 금액 정보 (TL 기준 실제 승인금액)
            .amount(info.approvedAmount())
            .vatAmount(info.vatAmount())
            .svcAmount(info.svcAmount())
            .installment(info.installment())
            // 승인/매출 시각(원거래일자/시간)
            .orgApprovedDate(info.approvedDate())
            .orgApprovedTime(info.approvedTime())
            // 승인번호
            .approvalNoRaw(info.approvalNoRaw())
            .approvalNo(info.approvalNo())
            // VAN 거래 식별 정보
            .vanTransactionNo(info.vanTransactionId())
            .terminalNo(info.terminalNo())
            .terminalId(info.terminalId())
            .terminalSequenceNo(info.terminalSeqNo())
            // 거래 속성
            .tranTypeCode(info.tranTypeCode())
            .mediaType(info.mediaType())
            // 부가정보
            .vanExtraRaw(info.vanExtraRaw());

    Payment payment = builder.build();

    // 상품 항목(PaymentItem) 생성 – 현재는 productId, optionText만 관리
    List<PaymentItem> items =
        req.items().stream()
            .map(item -> PaymentItem.builder().productId(item.productId()).optionText(null).build())
            .toList();

    items.forEach(payment::addItem);

    return payment;
  }

  // ===== 이하 레거시 헬퍼 (필요시 유지, 사용 안 하면 삭제해도 무방) =====

  private String extractApprovalNoRaw(byte[] data) {
    if (data == null || data.length < 60) {
      return "";
    }
    // 오래된 프로토콜 기준 오프셋 (cardNo(20)+amount(10)+tax(8)+svc(8)+inst(2) = 48)
    final int offset = 20 + 10 + 8 + 8 + 2;
    return new String(data, offset, 12, US_ASCII);
  }

  private LocalDate parseDate(String dateTime14) {
    return LocalDate.parse(dateTime14.substring(0, 8), DATE_FORMAT);
  }

  private LocalTime parseTime(String dateTime14) {
    return LocalTime.parse(dateTime14.substring(8, 14), TIME_FORMAT);
  }
}
