/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.wit.payment.domain.auth.service.AuthService;
import com.wit.payment.domain.user.dto.request.LoginRequest;
import com.wit.payment.domain.user.dto.response.UserResponse;
import com.wit.payment.global.response.BaseResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthControllerImpl implements AuthController {

  private final AuthService authService;

  @Override
  public ResponseEntity<BaseResponse<UserResponse>> login(
      @Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {

    UserResponse userResponse = authService.login(loginRequest, response);
    return ResponseEntity.ok(BaseResponse.success("로그인 성공", userResponse));
  }

  @Override
  public ResponseEntity<BaseResponse<String>> reissueAccessToken(
      HttpServletRequest request, HttpServletResponse response) {

    authService.reissueAccessToken(request, response);
    return ResponseEntity.ok(BaseResponse.success("AccessToken 재발급 성공"));
  }

  @Override
  public ResponseEntity<BaseResponse<Void>> logout(
      HttpServletRequest request, HttpServletResponse response) {

    authService.logout(request, response);
    return ResponseEntity.ok(BaseResponse.success("로그아웃 성공", null));
  }
}
