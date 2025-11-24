/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.kiosk.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "KioskResponse", description = "키오스크 응답 DTO")
public class KioskResponse {

  @Schema(description = "키오스크 ID", example = "1")
  private Long id;

  @Schema(description = "키오스크 이름", example = "1번 키오스크")
  private String name;
}
