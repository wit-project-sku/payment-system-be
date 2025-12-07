/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.pay.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "결제 상품 옵션(기종 등) 저장 요청 DTO")
public record PaymentItemOptionRequest(
    @Schema(description = "상품 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        @NotNull Long productId,
    @Schema(description = "옵션 텍스트(기종 등)", example = "iPhone 16 Pro") @NotBlank String optionText) {}
