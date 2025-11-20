/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.store.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "StoreResponse", description = "가게 정보 응답 DTO")
public class StoreResponse {

  @Schema(description = "가게 식별자", example = "1")
  private Long id;

  @Schema(description = "가게 이름", example = "인사동 한옥 카페")
  private String name;
}
