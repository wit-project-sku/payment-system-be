/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.wit.payment.domain.user.dto.request.LoginRequest;
import com.wit.payment.domain.user.dto.response.UserResponse;

/** 로그인, 로그아웃, 토큰 재발급 등을 처리하는 인증 서비스 인터페이스입니다. */
public interface AuthService {

  /**
   * 일반 로그인을 수행합니다.
   *
   * @param loginRequest 로그인 요청 정보 (로그인 아이디, 비밀번호)
   * @param response 액세스/리프레시 토큰 설정용 HTTP 응답
   * @return 로그인한 사용자 정보 응답
   */
  UserResponse login(LoginRequest loginRequest, HttpServletResponse response);

  /**
   * 현재 로그인된 사용자를 로그아웃 처리합니다.
   *
   * <p>AccessToken을 블랙리스트에 등록하고, Redis에 저장된 RefreshToken을 삭제하며, RefreshToken 쿠키를 만료시킵니다.
   *
   * @param request HTTP 요청 (Authorization 헤더에서 AccessToken 추출용)
   * @param response HTTP 응답 (RefreshToken 쿠키 삭제용)
   */
  void logout(HttpServletRequest request, HttpServletResponse response);

  /**
   * RefreshToken을 기반으로 새로운 AccessToken을 재발급합니다.
   *
   * <p>요청 쿠키에서 RefreshToken을 읽어와 유효성을 검증한 뒤, Redis에 저장된 RefreshToken과 일치할 경우 새로운 AccessToken을 생성해
   * Authorization 헤더에 설정합니다.
   *
   * @param request HTTP 요청 (쿠키에서 RefreshToken 추출용)
   * @param response HTTP 응답 (새로운 AccessToken 설정용)
   */
  void reissueAccessToken(HttpServletRequest request, HttpServletResponse response);

  /**
   * 현재 세션(토큰)을 조용히 무효화합니다.
   *
   * <p>내부적으로 {@link #logout(HttpServletRequest, HttpServletResponse)} 을 호출하되, 예외가 발생하더라도 그대로 흡수하여
   * 다른 비즈니스 로직(회원 탈퇴 등)에 영향을 주지 않도록 합니다.
   *
   * @param request HTTP 요청
   * @param response HTTP 응답
   */
  void invalidateCurrentSessionQuietly(HttpServletRequest request, HttpServletResponse response);
}
