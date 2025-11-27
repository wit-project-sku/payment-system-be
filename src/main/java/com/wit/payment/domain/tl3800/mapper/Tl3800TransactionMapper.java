/*
 * Copyright (c) WIT Global
 */
package com.wit.payment.domain.tl3800.mapper;

import com.wit.payment.domain.tl3800.dto.response.Tl3800ApproveResponse;
import com.wit.payment.domain.tl3800.dto.response.Tl3800CancelResponse;
import com.wit.payment.domain.tl3800.dto.response.Tl3800LastApprovalResponse;
import com.wit.payment.domain.tl3800.entity.Tl3800Transaction;
import java.time.LocalDateTime;

public final class Tl3800TransactionMapper {

  private Tl3800TransactionMapper() {
  }

  public static Tl3800ApproveResponse toApproveResponse(Tl3800Transaction tx) {
    return Tl3800ApproveResponse.builder()
        .transactionId(tx.getId())
        .approvalNo(tx.getApprovalNo())
        .amount(tx.getAmount())
        .responseCode(tx.getResponseCode())
        .responseMessage(tx.getResponseMessage())
        .cardNoMasked(tx.getCardNoMasked())
        .mediaType(tx.getMediaType())
        .approvedAt(tx.getApprovedAt())
        .build();
  }

  public static Tl3800CancelResponse toCancelResponse(Tl3800Transaction tx) {
    return Tl3800CancelResponse.builder()
        .transactionId(tx.getId())
        .approvalNo(tx.getApprovalNo())
        .amount(tx.getAmount())
        .responseCode(tx.getResponseCode())
        .responseMessage(tx.getResponseMessage())
        .canceledAt(tx.getApprovedAt())
        .build();
  }

  public static Tl3800LastApprovalResponse toLastApprovalResponse(Tl3800Transaction tx) {
    return Tl3800LastApprovalResponse.builder()
        .transactionId(tx.getId())
        .approvalNo(tx.getApprovalNo())
        .amount(tx.getAmount())
        .responseCode(tx.getResponseCode())
        .responseMessage(tx.getResponseMessage())
        .approvedAt(tx.getApprovedAt())
        .build();
  }

  public static LocalDateTime now() {
    return LocalDateTime.now();
  }
}