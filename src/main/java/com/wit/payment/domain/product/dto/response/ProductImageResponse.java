/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.product.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "ProductImageResponse", description = "상품 이미지 응답 DTO")
public class ProductImageResponse {

  @Schema(description = "이미지 식별자", example = "1")
  private Long id;

  @Schema(description = "이미지 URL")
  private String imageUrl;

  @Schema(description = "이미지 순서", example = "0")
  private Integer orderNum;
}
