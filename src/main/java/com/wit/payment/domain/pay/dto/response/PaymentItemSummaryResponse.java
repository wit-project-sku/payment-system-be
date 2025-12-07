/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.pay.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "결제 내 개별 상품 요약 DTO")
@Builder
public record PaymentItemSummaryResponse(
    @Schema(description = "상품 ID", example = "1") Long productId,
    @Schema(description = "선택한 옵션(기종 등)", example = "아이폰 16 프로") String optionText) {}
