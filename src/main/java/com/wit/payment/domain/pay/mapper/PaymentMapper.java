/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.pay.mapper;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Component;

import com.wit.payment.domain.pay.dto.response.PaymentIssueResponse;
import com.wit.payment.domain.pay.dto.response.PaymentSummaryResponse;
import com.wit.payment.domain.pay.entity.Payment;
import com.wit.payment.domain.pay.entity.PaymentIssue;
import com.wit.payment.domain.pay.entity.PaymentIssueStatus;
import com.wit.payment.global.tl3800.proto.TLPacket;

@Component
public class PaymentMapper {

  private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
  private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HHmmss");

  /** TL 승인 응답 + 금액/할부/배송/전화번호/이미지 → Payment 엔티티 변환 */
  public Payment toPayment(
      TLPacket packet,
      long amount,
      String inst,
      boolean delivery,
      String phoneNumber,
      String imageUrl) {

    LocalDate approvedDate = parseDate(packet.dateTime14);
    LocalTime approvedTime = parseTime(packet.dateTime14);

    String approvalNoRaw = extractApprovalNoRaw(packet.data);
    String approvalNo = approvalNoRaw.trim();

    return Payment.builder()
        .approvedDate(approvedDate)
        .approvedTime(approvedTime)
        .approvalNoRaw(approvalNoRaw)
        .approvalNo(approvalNo)
        .amount((int) amount)
        .installment(inst)
        .phoneNumber(phoneNumber)
        .deliveryAddress(delivery ? "" : null)
        .imageUrl(imageUrl)
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

  public PaymentSummaryResponse toPaymentResponse(Payment payment) {
    return PaymentSummaryResponse.builder()
        .paymentId(payment.getId())
        .approvedDate(payment.getApprovedDate())
        .approvedTime(payment.getApprovedTime())
        .approvalNo(payment.getApprovalNo())
        .amount(payment.getAmount())
        .installment(payment.getInstallment())
        .phoneNumber(payment.getPhoneNumber())
        .deliveryAddress(payment.getDeliveryAddress())
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

  private String extractApprovalNoRaw(byte[] data) {
    if (data == null || data.length < 60) {
      return "";
    }
    final int offset = 20 + 10 + 8 + 8 + 2;
    return new String(data, offset, 12, StandardCharsets.US_ASCII);
  }

  private LocalDate parseDate(String dateTime14) {
    return LocalDate.parse(dateTime14.substring(0, 8), DATE_FORMAT);
  }

  private LocalTime parseTime(String dateTime14) {
    return LocalTime.parse(dateTime14.substring(8, 14), TIME_FORMAT);
  }
}
