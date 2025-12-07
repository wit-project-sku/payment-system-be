/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.pay.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "전화번호 기준 결제 + 구매 상품 목록 응답 DTO")
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PaymentWithItemsResponse(
    @Schema(description = "결제 ID", example = "10") Long paymentId,
    @Schema(description = "배송 요약 주소 (있다면)", example = "서울시 종로구 ...") String deliveryAddress,
    @Schema(description = "결제에 포함된 상품 목록") List<PaymentItemSummaryResponse> items) {}
