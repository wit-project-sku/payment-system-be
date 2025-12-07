/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.pay.dto.request;

import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "전화번호 기준 결제 이력 조회 요청 DTO")
public record PaymentHistoryByPhoneRequest(
    @Schema(
            description = "조회할 고객 전화번호",
            example = "01012345678",
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        String phoneNumber) {}
