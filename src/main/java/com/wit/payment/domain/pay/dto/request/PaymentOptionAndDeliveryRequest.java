/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.pay.dto.request;

import java.util.List;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "결제 옵션 및 배송지 저장 요청 DTO")
public record PaymentOptionAndDeliveryRequest(
    @Schema(description = "상품별 옵션(기종 등) 정보 목록", requiredMode = Schema.RequiredMode.REQUIRED) @Valid
        List<PaymentItemOptionRequest> items,
    @Schema(
            description = "배송지 정보 (현장 수령이면 null 로 보낼 수 있음)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @Valid
        DeliverySaveRequest delivery) {}
