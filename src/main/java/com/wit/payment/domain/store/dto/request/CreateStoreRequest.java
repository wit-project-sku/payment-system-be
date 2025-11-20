/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.store.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "CreateStoreRequest", description = "가게 생성 요청 DTO")
public class CreateStoreRequest {

  @Schema(description = "가게 이름", example = "인사동 한옥 카페")
  private String name;
}
