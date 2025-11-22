/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.global.security;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.wit.payment.domain.auth.exception.AuthErrorCode;
import com.wit.payment.global.exception.CustomException;

@Component
public class SecurityUtil {

  /**
   * 현재 SecurityContext에 저장된 사용자 ID를 반환합니다.
   *
   * @throws CustomException 인증 정보가 없거나 타입이 올바르지 않은 경우
   */
  public static Long getCurrentUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
      throw new CustomException(AuthErrorCode.INVALID_AUTH_CONTEXT);
    }

    Object principal = authentication.getPrincipal();
    if (!(principal instanceof CustomUserDetails userDetails)) {
      throw new CustomException(AuthErrorCode.AUTHENTICATION_NOT_FOUND);
    }

    return userDetails.getUserId();
  }

  /** 현재 사용자가 인증된 상태인지 여부를 반환합니다. */
  public static boolean isAuthenticated() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication != null && authentication.isAuthenticated();
  }

  /**
   * HttpServletRequest의 Authorization 헤더에서 Bearer AccessToken을 추출합니다.
   *
   * @param request HTTP 요청
   * @return 토큰 문자열 (없으면 null)
   */
  public static String resolveAccessToken(HttpServletRequest request) {
    String bearer = request.getHeader("Authorization");
    if (bearer != null && bearer.startsWith("Bearer ")) {
      return bearer.substring(7);
    }
    return null;
  }
}
