/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.user.mapper;

import org.springframework.stereotype.Component;

import com.wit.payment.domain.user.dto.request.InfoRequest;
import com.wit.payment.domain.user.dto.response.UserResponse;
import com.wit.payment.domain.user.entity.Role;
import com.wit.payment.domain.user.entity.User;

@Component
public class UserMapper {

  /**
   * 회원가입 요청 DTO와 인코딩된 비밀번호를 기반으로 User 엔티티를 생성합니다.
   *
   * <p>기본 역할(role)은 USER로 설정합니다.
   *
   * @param request 회원 정보 요청 DTO
   * @param encodedPassword 인코딩된 비밀번호
   * @return 신규 생성된 User 엔티티
   */
  public User toUser(InfoRequest request, String encodedPassword) {
    return User.builder()
        .loginId(request.loginId())
        .password(encodedPassword)
        .name(request.name())
        .role(Role.USER) // 기본 가입은 일반 사용자
        .build();
  }

  /**
   * User 엔티티를 사용자 응답 DTO로 변환합니다.
   *
   * @param user 사용자 엔티티
   * @return 사용자 응답 DTO
   */
  public UserResponse toResponse(User user) {
    if (user == null) {
      return null;
    }

    return new UserResponse(
        user.getId(),
        user.getLoginId(),
        user.getName(),
        user.getRole() != null ? user.getRole().name() : null);
  }
}
