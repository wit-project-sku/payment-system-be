/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.product.dto.request;

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
@Schema(title = "UpdateProductRequest", description = "상품 수정 요청 DTO")
public class UpdateProductRequest {

  @Schema(description = "상품 이름", example = "아이스 아메리카노")
  private String name;

  @Schema(description = "상품 서브 제목", example = "시원한 여름 시그니처 메뉴")
  private String subTitle;

  @Schema(description = "상품 가격", example = "4500")
  private Integer price;

  @Schema(description = "상품 설명")
  private String description;

  @Schema(description = "상품 상태", example = "ON_SALE")
  private ProductStatus status;
}
