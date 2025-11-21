/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.category.dto.response;

import java.util.List;

import com.wit.payment.domain.product.dto.response.ProductSummaryResponse;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    title = "FirstCategoryResponse",
    description = "초기 화면 구성용 데이터. 전체 카테고리 목록과 첫 번째 카테고리의 상품 목록을 포함합니다.")
public record FirstCategoryResponse(
    @Schema(
            description = "전체 카테고리 목록",
            example = "[{\"id\":1,\"name\":\"WITH Goods\"}, {\"id\":2, \"name\":\"Custom Goods\"}]")
        List<CategoryResponse> categorites,
    @Schema(description = "첫 번째 카테고리의 식별자", example = "1") Long firstCategoryId,
    @Schema(description = "첫 번째 카테고리의 이름", example = "WITH Goods") String firstCategoryName,
    @Schema(
            description = "첫 번째 카테고리의 상품 목록",
            example = "[{ \"id\": 10, \"name\": \"AR 합성 머그컵\", \"price\": 24900 }]")
        List<ProductSummaryResponse> firstCategoryProducts) {}
