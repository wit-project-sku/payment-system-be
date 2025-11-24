/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.kiosk.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "KioskRequest", description = "키오스크 수정 요청 DTO")
public class KioskRequest {

  @Schema(description = "키오스크 이름", example = "1번 키오스크-수정")
  @NotBlank(message = "키오스크 이름은 필수 값입니다.")
  @Size(max = 50, message = "키오스크 이름은 최대 50자까지 입력 가능합니다.")
  private String name;
}
