/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "LoginRequest", description = "로그인 요청 DTO")
public record LoginRequest(
    @NotBlank(message = "로그인 아이디는 필수입니다.")
        @Size(min = 4, max = 20, message = "로그인 아이디는 4~20자여야 합니다.")
        @Schema(description = "아이디", example = "wisk", minLength = 4, maxLength = 20)
        String loginId,
    @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(min = 8, max = 20, message = "비밀번호는 8~20자여야 합니다.")
        @Schema(description = "비밀번호", example = "qwer1234!", minLength = 8, maxLength = 20)
        String password) {}
