/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.product.dto.response;

import com.wit.payment.domain.product.entity.ProductStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "ProductSummaryResponse", description = "상품 목록 조회 응답 DTO")
public class ProductSummaryResponse {

  @Schema(description = "상품 식별자", example = "1")
  private Long id;

  @Schema(description = "카테고리 이름", example = "WITH Goods")
  private String categoryName;

  @Schema(description = "상품 이름", example = "아이스 아메리카노")
  private String name;

  @Schema(description = "상품 서브 제목", example = "시원한 여름 시그니처 메뉴")
  private String subTitle;

  @Schema(description = "상품 가격", example = "4500")
  private Integer price;

  @Schema(description = "상품 상태", example = "ON_SALE")
  private ProductStatus status;

  @Schema(description = "대표 이미지 URL")
  private String thumbnailImageUrl;
}
