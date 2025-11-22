/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "InfoRequest", description = "회원 정보 요청 DTO")
public record InfoRequest(
    @NotBlank(message = "로그인 아이디는 필수입니다.")
        @Size(min = 4, max = 20, message = "로그인 아이디는 4~20자여야 합니다.")
        @Pattern(regexp = "^[A-Za-z0-9]+$", message = "아이디는 영문과 숫자만 사용할 수 있습니다.")
        @Schema(description = "아이디", example = "wisk", minLength = 4, maxLength = 20)
        String loginId,
    @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(min = 8, max = 20, message = "비밀번호는 8~20자여야 합니다.")
        @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*?&])[A-Za-z\\d$@$!%*?&]{8,}$",
            message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다.")
        @Schema(description = "비밀번호", example = "qwer1234!", minLength = 8, maxLength = 20)
        String password,
    @NotBlank(message = "이름은 필수입니다.")
        @Size(min = 2, max = 20, message = "이름은 2~20자여야 합니다.")
        @Pattern(regexp = "^[가-힣A-Za-z]+$", message = "이름은 한글 또는 영문만 가능합니다.")
        @Schema(description = "사용자 이름", example = "나경", minLength = 2, maxLength = 20)
        String name) {}
