/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UserResponse", description = "사용자 정보 응답 DTO")
public record UserResponse(
    @Schema(description = "사용자 식별자", example = "1") Long id,
    @Schema(description = "로그인 아이디", example = "wit") String loginId,
    @Schema(description = "사용자 이름", example = "나경") String name,
    @Schema(description = "사용자 역할 (USER, ADMIN)", example = "USER") String role) {}
