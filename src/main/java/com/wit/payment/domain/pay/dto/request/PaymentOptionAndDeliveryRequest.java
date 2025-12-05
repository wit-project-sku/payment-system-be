/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.pay.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "결제 옵션 + 배송 정보 저장 요청 DTO")
public record PaymentOptionAndDeliveryRequest(
    @Schema(
            description = "고객 전화번호",
            example = "01012345678",
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        String phoneNumber,
    @Schema(description = "상품 옵션 목록(기종 등)", requiredMode = Schema.RequiredMode.NOT_REQUIRED) @Valid
        List<PaymentItemOptionRequest> items,
    @Schema(description = "배송 정보 (nullable)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @Valid
        DeliverySaveRequest delivery) {}
