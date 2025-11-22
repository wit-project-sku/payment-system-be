/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.global.jwt;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.wit.payment.domain.auth.exception.AuthErrorCode;
import com.wit.payment.global.exception.CustomException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtProvider {

  private Key key;
  private final String secretKey;

  @Getter private final long accessTokenExpireTime;

  @Getter private final long refreshTokenExpireTime;

  public JwtProvider(
      @Value("${spring.jwt.secret}") String secretKey,
      @Value("${spring.jwt.access-token-expire-time}") long accessTokenExpireTime,
      @Value("${spring.jwt.refresh-token-expire-time}") long refreshTokenExpireTime) {
    this.secretKey = secretKey;
    this.accessTokenExpireTime = accessTokenExpireTime;
    this.refreshTokenExpireTime = refreshTokenExpireTime;
  }

  @PostConstruct
  public void init() {
    byte[] keyBytes = Base64.getDecoder().decode(secretKey);
    this.key = Keys.hmacShaKeyFor(keyBytes);
  }

  /**
   * 사용자 정보(userId, loginId, role)를 기반으로 AccessToken을 생성합니다.
   *
   * @param userId 사용자 PK
   * @param loginId 로그인 아이디
   * @param role 사용자 역할 (USER / ADMIN)
   */
  public String createAccessToken(Long userId, String loginId, String role) {
    return createToken(userId, loginId, role, accessTokenExpireTime);
  }

  /**
   * RefreshToken은 userId만 포함하여 생성합니다.
   *
   * @param userId 사용자 PK
   */
  public String createRefreshToken(Long userId) {
    return createToken(userId, null, null, refreshTokenExpireTime);
  }

  /**
   * JWT 토큰을 생성합니다.
   *
   * @param userId 사용자 PK (subject)
   * @param loginId 로그인 아이디 (AccessToken에만 사용, RefreshToken이면 null 가능)
   * @param role 사용자 역할 (AccessToken에만 사용, RefreshToken이면 null 가능)
   * @param expireTimeMillis 만료 시간(ms)
   */
  private String createToken(Long userId, String loginId, String role, long expireTimeMillis) {
    Date now = new Date();

    var builder =
        Jwts.builder()
            .setSubject(String.valueOf(userId))
            .setId(UUID.randomUUID().toString())
            .setIssuedAt(now)
            .setExpiration(new Date(now.getTime() + expireTimeMillis))
            .signWith(key, SignatureAlgorithm.HS256);

    // AccessToken인 경우에만 loginId, role을 claim으로 추가
    if (loginId != null) {
      builder.claim("loginId", loginId);
    }
    if (role != null) {
      builder.claim("role", role);
    }

    return builder.compact();
  }

  /**
   * 토큰의 유효성을 검증합니다.
   *
   * @throws CustomException 유효하지 않은 토큰 유형/형식/서명/만료 등
   */
  public boolean validateToken(String token) {
    try {
      parseClaims(token);
      return true;
    } catch (ExpiredJwtException e) {
      throw new CustomException(AuthErrorCode.JWT_TOKEN_EXPIRED);
    } catch (UnsupportedJwtException e) {
      throw new CustomException(AuthErrorCode.UNSUPPORTED_TOKEN);
    } catch (MalformedJwtException e) {
      throw new CustomException(AuthErrorCode.MALFORMED_JWT_TOKEN);
    } catch (io.jsonwebtoken.security.SignatureException e) {
      throw new CustomException(AuthErrorCode.INVALID_SIGNATURE);
    } catch (IllegalArgumentException e) {
      throw new CustomException(AuthErrorCode.ILLEGAL_ARGUMENT);
    }
  }

  /** 토큰에서 userId(subject)를 추출합니다. */
  public Long extractUserId(String token) {
    return Long.parseLong(parseClaims(token).getSubject());
  }

  /** 토큰에서 loginId를 추출합니다. (AccessToken 전용) */
  public String extractLoginId(String token) {
    return parseClaims(token).get("loginId", String.class);
  }

  /** 토큰에서 역할(role)을 추출합니다. (AccessToken 전용) */
  public String extractRole(String token) {
    return parseClaims(token).get("role", String.class);
  }

  /** 토큰의 jti(id)를 추출합니다. */
  public String extractTokenId(String token) {
    return parseClaims(token).getId();
  }

  /** 토큰 만료 시각을 추출합니다. */
  public Date extractExpiration(String token) {
    return parseClaims(token).getExpiration();
  }

  /** 내부적으로 JWT Claims를 파싱합니다. */
  private Claims parseClaims(String token) {
    return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
  }
}
