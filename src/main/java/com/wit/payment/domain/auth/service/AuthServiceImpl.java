/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.auth.service;

import java.time.Duration;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wit.payment.domain.auth.exception.AuthErrorCode;
import com.wit.payment.domain.user.dto.request.LoginRequest;
import com.wit.payment.domain.user.dto.response.UserResponse;
import com.wit.payment.domain.user.entity.User;
import com.wit.payment.domain.user.mapper.UserMapper;
import com.wit.payment.domain.user.repository.UserRepository;
import com.wit.payment.global.exception.CustomException;
import com.wit.payment.global.jwt.JwtProvider;
import com.wit.payment.global.redis.RedisUtil;
import com.wit.payment.global.security.SecurityUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

  private static final String REFRESH_TOKEN_PREFIX = "user:refresh:";
  private static final String BLACKLIST_PREFIX = "blacklist:";

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtProvider jwtProvider;
  private final RedisUtil redisUtil;
  private final UserMapper userMapper;

  @Value("${cookie.secure}")
  private boolean secure;

  @Override
  public UserResponse login(LoginRequest loginRequest, HttpServletResponse response) {
    User user = validateUserCredentials(loginRequest);
    UserResponse userResponse = issueTokensAndSetResponse(user, response);

    log.info(
        "로그인 성공 - userId: {}, loginId: {}, role: {}",
        user.getId(),
        user.getLoginId(),
        user.getRole());
    return userResponse;
  }

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response) {
    String accessToken = SecurityUtil.resolveAccessToken(request);

    if (accessToken == null || !jwtProvider.validateToken(accessToken)) {
      log.warn("로그아웃 실패 - 유효하지 않은 AccessToken: {}", accessToken);
      throw new CustomException(AuthErrorCode.INVALID_ACCESS_TOKEN);
    }

    Long userId = jwtProvider.extractUserId(accessToken);

    long expirationMillis =
        jwtProvider.extractExpiration(accessToken).getTime() - System.currentTimeMillis();
    long expirationSeconds = expirationMillis / 1000;

    redisUtil.setData(BLACKLIST_PREFIX + accessToken, "logout", expirationSeconds);
    redisUtil.deleteData(REFRESH_TOKEN_PREFIX + userId);
    deleteRefreshTokenCookie(response);

    log.info("로그아웃 성공 - userId: {}", userId);
  }

  @Override
  public void reissueAccessToken(HttpServletRequest request, HttpServletResponse response) {
    String refreshToken = extractRefreshTokenFromCookie(request);
    if (refreshToken == null || !jwtProvider.validateToken(refreshToken)) {
      log.warn("액세스 토큰 재발급 실패 - RefreshToken 없음 또는 검증 실패");
      throw new CustomException(AuthErrorCode.REFRESH_TOKEN_REQUIRED);
    }

    Long userId = jwtProvider.extractUserId(refreshToken);
    String storedToken = redisUtil.getData(REFRESH_TOKEN_PREFIX + userId);

    if (!refreshToken.equals(storedToken)) {
      log.warn("액세스 토큰 재발급 실패 - 저장된 RefreshToken과 불일치. userId: {}", userId);
      throw new CustomException(AuthErrorCode.REFRESH_TOKEN_REQUIRED);
    }

    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new CustomException(AuthErrorCode.AUTHENTICATION_NOT_FOUND));

    String newAccessToken =
        jwtProvider.createAccessToken(user.getId(), user.getLoginId(), user.getRole().name());
    setAccessTokenHeader(response, newAccessToken);

    log.info("액세스 토큰 재발급 성공 - userId: {}", userId);
  }

  @Override
  public void invalidateCurrentSessionQuietly(
      HttpServletRequest request, HttpServletResponse response) {
    try {
      logout(request, response);
    } catch (CustomException ex) {
      log.warn("세션 무효화 중 예외 발생 - message: {}", ex.getMessage());
      deleteRefreshTokenCookie(response);
    }
  }

  /** 로그인 아이디/비밀번호를 검증하여 유효한 사용자 엔티티를 반환합니다. */
  private User validateUserCredentials(LoginRequest loginRequest) {
    User user =
        userRepository
            .findByLoginId(loginRequest.loginId())
            .orElseThrow(() -> new CustomException(AuthErrorCode.INVALID_PASSWORD));

    if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
      throw new CustomException(AuthErrorCode.INVALID_PASSWORD);
    }

    return user;
  }

  /** AccessToken/RefreshToken 발급 + 응답 세팅 + UserResponse 생성 */
  private UserResponse issueTokensAndSetResponse(User user, HttpServletResponse response) {
    String accessToken =
        jwtProvider.createAccessToken(user.getId(), user.getLoginId(), user.getRole().name());
    String refreshToken = jwtProvider.createRefreshToken(user.getId());

    long refreshTokenExpireSeconds = jwtProvider.getRefreshTokenExpireTime() / 1000;
    redisUtil.setData(REFRESH_TOKEN_PREFIX + user.getId(), refreshToken, refreshTokenExpireSeconds);

    setAccessTokenHeader(response, accessToken);
    setRefreshTokenCookie(response, refreshToken, refreshTokenExpireSeconds);

    return userMapper.toResponse(user);
  }

  private void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
    response.setHeader("Authorization", "Bearer " + accessToken);
  }

  /** RefreshToken을 HttpOnly 쿠키로 설정 */
  private void setRefreshTokenCookie(
      HttpServletResponse response, String refreshToken, long maxAgeSec) {
    ResponseCookie.ResponseCookieBuilder cookieBuilder =
        ResponseCookie.from("refreshToken", refreshToken)
            .httpOnly(true)
            .path("/")
            .maxAge(Duration.ofSeconds(maxAgeSec));

    if (secure) {
      cookieBuilder.secure(true).sameSite("None").domain(".witapp.net");
    } else {
      cookieBuilder.secure(false).sameSite("Lax");
    }

    response.addHeader(HttpHeaders.SET_COOKIE, cookieBuilder.build().toString());
  }

  private String extractRefreshTokenFromCookie(HttpServletRequest request) {
    if (request.getCookies() == null) {
      return null;
    }

    for (Cookie cookie : request.getCookies()) {
      if ("refreshToken".equals(cookie.getName())) {
        return cookie.getValue();
      }
    }
    return null;
  }

  /** refreshToken 쿠키를 즉시 만료 */
  private void deleteRefreshTokenCookie(HttpServletResponse response) {
    ResponseCookie.ResponseCookieBuilder cookieBuilder =
        ResponseCookie.from("refreshToken", "").httpOnly(true).path("/").maxAge(Duration.ZERO);

    if (secure) {
      cookieBuilder.secure(true).sameSite("None").domain(".witapp.net");
    } else {
      cookieBuilder.secure(false).sameSite("Lax");
    }

    response.addHeader(HttpHeaders.SET_COOKIE, cookieBuilder.build().toString());
  }
}
