/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.user.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.wit.payment.domain.user.dto.request.InfoRequest;
import com.wit.payment.domain.user.dto.response.UserResponse;

/** 회원 가입, 조회, 탈퇴 등 사용자 비즈니스 로직을 처리하는 서비스입니다. */
public interface UserService {

  /**
   * 새로운 사용자를 회원가입 처리합니다.
   *
   * @param request 회원가입 요청 정보 (로그인 아이디, 비밀번호, 이름)
   * @return 생성된 사용자 정보 응답
   */
  UserResponse register(InfoRequest request);

  /**
   * 현재 로그인한 사용자의 정보를 조회합니다.
   *
   * @return 사용자 정보 응답
   */
  UserResponse getMyInfo();

  /**
   * 현재 로그인한 사용자를 탈퇴 처리합니다.
   *
   * @param request HTTP 요청 (세션/토큰 무효화를 위한 정보)
   * @param response HTTP 응답 (쿠키 삭제 등)
   */
  void deleteMyAccount(HttpServletRequest request, HttpServletResponse response);
}
