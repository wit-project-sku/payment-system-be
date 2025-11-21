/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.category.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "CategoryResponse", description = "카테고리 정보 응답 DTO")
public class CategoryResponse {

  @Schema(description = "카테고리 식별자", example = "1")
  private Long id;

  @Schema(description = "카테고리 이름", example = "WITH Goods")
  private String name;
}
