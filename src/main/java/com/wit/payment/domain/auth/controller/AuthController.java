/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.wit.payment.domain.user.dto.request.LoginRequest;
import com.wit.payment.domain.user.dto.response.UserResponse;
import com.wit.payment.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "인증 관련 API")
public interface AuthController {

  @Operation(summary = "로그인 API", description = "아이디과 비밀번호로 로그인합니다.")
  @PostMapping("/login")
  ResponseEntity<BaseResponse<UserResponse>> login(
      @Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response);

  @Operation(summary = "액세스 토큰 재발급 API", description = "쿠키의 리프레시 토큰으로 새로운 액세스 토큰을 발급합니다.")
  @PostMapping("/refresh")
  ResponseEntity<BaseResponse<String>> reissueAccessToken(
      HttpServletRequest request, HttpServletResponse response);

  @Operation(summary = "로그아웃 API", description = "AccessToken·RefreshToken을 만료 처리합니다.")
  @PostMapping("/logout")
  ResponseEntity<BaseResponse<Void>> logout(
      HttpServletRequest request, HttpServletResponse response);
}
