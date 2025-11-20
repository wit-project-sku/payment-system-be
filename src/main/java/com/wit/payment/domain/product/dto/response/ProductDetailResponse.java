/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.product.dto.response;

import java.util.List;

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
@Schema(title = "ProductDetailResponse", description = "상품 상세 조회 응답 DTO")
public class ProductDetailResponse {

  @Schema(description = "상품 식별자", example = "1")
  private Long id;

  @Schema(description = "가게 이름", example = "인사동 한옥 카페")
  private String storeName;

  @Schema(description = "상품 이름")
  private String name;

  @Schema(description = "상품 서브 제목")
  private String subTitle;

  @Schema(description = "상품 가격")
  private Integer price;

  @Schema(description = "상품 설명")
  private String description;

  @Schema(description = "상품 상태")
  private ProductStatus status;

  @Schema(description = "상품 이미지 리스트(모든 이미지)")
  private List<ProductImageResponse> images;
}
