/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.pay.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

import com.wit.payment.domain.pay.entity.PaymentIssueStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "결제 이슈 내역 응답")
@Builder
public record PaymentIssueResponse(
    @Schema(description = "결제 이슈 ID", example = "1") Long paymentIssueId,
    @Schema(description = "이슈 발생 일자", example = "2025-12-04") LocalDate occurredDate,
    @Schema(description = "이슈 발생 시각", example = "13:50:10") LocalTime occurredTime,
    @Schema(description = "이슈 발생 시 결제금액(원)", example = "10") Integer amount,
    @Schema(description = "이슈 내용", example = "단말 응답 타임아웃") String message,
    @Schema(description = "이슈 처리 상태", example = "UNRESOLVED") PaymentIssueStatus status) {}
