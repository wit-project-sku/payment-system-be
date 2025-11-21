/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.category.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "CreateCategoryRequest", description = "카테고리 생성 요청 DTO")
public class CreateCategoryRequest {

  @Schema(description = "카테고리 이름", example = "WITH Goods")
  private String name;
}
