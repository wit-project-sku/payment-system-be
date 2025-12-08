/*
 * Copyright (c) WIT Global
 */
package com.wit.payment.domain.pay.service;

import com.wit.payment.domain.pay.dto.request.PayFailureReportRequest;
import com.wit.payment.domain.pay.dto.request.PaySuccessReportRequest;
import com.wit.payment.domain.pay.entity.Payment;
import com.wit.payment.domain.pay.entity.PaymentIssue;
import com.wit.payment.domain.pay.mapper.PaymentMapper;
import com.wit.payment.domain.pay.repository.PaymentIssueRepository;
import com.wit.payment.domain.pay.repository.PaymentRepository;
import com.wit.payment.global.tl3800.parser.TL3800ApprovalInfo;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PayReportService {

  private final PaymentRepository paymentRepository;
  private final PaymentIssueRepository paymentIssueRepository;
  private final PaymentMapper paymentMapper;

  @Transactional
  public void reportSuccess(PaySuccessReportRequest request) {

    log.info(
        "[REPORT] 로컬 결제 성공 보고 수신 - phone={}, amount={}, items={}",
        request.payRequest().phoneNumber(),
        request.approvedAmount(),
        request.payRequest().items());

    // 1. TL 패킷 파싱
    TL3800ApprovalInfo tlInfo = TL3800ApprovalInfo.fromHex(request.tlPacketHex());

    // 2. Payment 엔티티 생성
    Payment payment = paymentMapper.toPayment(request, tlInfo);

    // 3. 저장
    Payment saved = paymentRepository.save(payment);

    // 4. sms 전송

    log.info(
        "[REPORT] 결제 저장 완료 - paymentId={}, approvalNo={}, vanTxNo={}",
        saved.getId(),
        saved.getApprovalNo(),
        saved.getVanTransactionNo());
  }

  @Transactional
  public void reportFailure(PayFailureReportRequest request) {

    log.warn(
        "[REPORT] 로컬 결제 실패/예외 보고 수신 - phone={}, amount={}, reason={}, respCode={}",
        request.payRequest().phoneNumber(),
        request.requestedAmount(),
        request.reason(),
        request.respCode());

    // 1. 이슈 발생일시 결정 (TL 패킷 있으면 그 시간, 없으면 now)
    LocalDate occurredDate;
    LocalTime occurredTime;

    if (request.tlPacketHex() != null && !request.tlPacketHex().isBlank()) {
      try {
        TL3800ApprovalInfo tlInfo = TL3800ApprovalInfo.fromHex(request.tlPacketHex());
        occurredDate = tlInfo.approvedDate();
        occurredTime = tlInfo.approvedTime();
      } catch (Exception e) {
        log.warn("[REPORT] 실패 보고 TL 패킷 파싱 실패 → now()로 대체 - ex={}", e.toString());
        occurredDate = LocalDate.now();
        occurredTime = LocalTime.now();
      }
    } else {
      // 단말 응답 자체가 없던 케이스(통신 끊김 등)
      occurredDate = LocalDate.now();
      occurredTime = LocalTime.now();
    }

    // 2. 메시지 구성 (respCode 있으면 prefix)
    String message =
        (request.respCode() != null)
            ? "[respCode=" + request.respCode() + "] " + request.reason()
            : request.reason();

    // 3. PaymentIssue 생성 및 저장
    PaymentIssue issue =
        PaymentIssue.builder()
            .occurredDate(occurredDate)
            .occurredTime(occurredTime)
            .amount((int) request.requestedAmount())
            .message(message)
            .phoneNumber(request.payRequest().phoneNumber())
            .build();

    PaymentIssue saved = paymentIssueRepository.save(issue);

    log.info(
        "[REPORT] 결제 이슈 저장 완료 - issueId={}, occurredDate={}, occurredTime={}",
        saved.getId(),
        saved.getOccurredDate(),
        saved.getOccurredTime());
  }
}
