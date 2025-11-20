/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.store.dto.response;

import java.util.List;

import com.wit.payment.domain.product.dto.response.ProductSummaryResponse;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    title = "FirstStoreResponse",
    description = "초기 화면 구성용 데이터. 전체 가게 목록과 첫 번째 가게의 상품 목록을 포함합니다.")
public record FirstStoreResponse(
    @Schema(
            description = "전체 가게 목록",
            example = "[{\"id\":1,\"name\":\"WITH Goods\"}, {\"id\":2, \"name\":\"Custom Goods\"}]")
        List<StoreResponse> stores,
    @Schema(description = "첫 번째 가게의 식별자", example = "1") Long firstStoreId,
    @Schema(description = "첫 번째 가게의 이름", example = "WITH Goods") String firstStoreName,
    @Schema(
            description = "첫 번째 가게의 상품 목록",
            example = "[{ \"id\": 10, \"name\": \"AR 합성 머그컵\", \"price\": 24900 }]")
        List<ProductSummaryResponse> firstStoreProducts) {}
