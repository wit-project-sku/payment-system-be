/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.wit.payment.domain.user.dto.request.InfoRequest;
import com.wit.payment.domain.user.dto.response.UserResponse;
import com.wit.payment.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/** 회원 가입, 정보 조회, 탈퇴 등 사용자 관련 API를 제공하는 컨트롤러입니다. */
@RequestMapping("/api/users")
@Tag(name = "User", description = "회원 관련 API")
public interface UserController {

  @Operation(summary = "회원가입 API", description = "새로운 사용자를 회원가입 처리합니다.")
  @PostMapping("/register")
  ResponseEntity<BaseResponse<UserResponse>> register(@RequestBody @Valid InfoRequest request);

  @Operation(summary = "내 정보 조회 API", description = "현재 로그인한 사용자의 기본 정보를 조회합니다.")
  @GetMapping
  ResponseEntity<BaseResponse<UserResponse>> getMyInfo();

  @Operation(summary = "회원 탈퇴 API", description = "현재 로그인한 사용자를 탈퇴 처리합니다.")
  @DeleteMapping
  ResponseEntity<BaseResponse<Void>> deleteMyAccount(
      HttpServletRequest request, HttpServletResponse response);
}
