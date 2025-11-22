/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.user.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wit.payment.domain.auth.service.AuthService;
import com.wit.payment.domain.user.dto.request.InfoRequest;
import com.wit.payment.domain.user.dto.response.UserResponse;
import com.wit.payment.domain.user.entity.Role;
import com.wit.payment.domain.user.entity.User;
import com.wit.payment.domain.user.exception.UserErrorCode;
import com.wit.payment.domain.user.mapper.UserMapper;
import com.wit.payment.domain.user.repository.UserRepository;
import com.wit.payment.global.exception.CustomException;
import com.wit.payment.global.security.SecurityUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** 회원 관련 비즈니스 로직을 구현한 서비스 클래스입니다. */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;
  private final AuthService authService;

  @Override
  public UserResponse register(InfoRequest request) {

    if (userRepository.existsByLoginId(request.loginId())) {
      log.warn("회원가입 실패 - 중복 loginId: {}", request.loginId());
      throw new CustomException(UserErrorCode.DUPLICATE_LOGIN_ID);
    }

    String encodedPassword = passwordEncoder.encode(request.password());

    User user =
        User.builder()
            .loginId(request.loginId())
            .password(encodedPassword)
            .name(request.name())
            .role(Role.USER)
            .build();

    User saved = userRepository.save(user);
    log.info("회원가입 성공 - userId: {}, loginId: {}", saved.getId(), saved.getLoginId());

    return userMapper.toResponse(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public UserResponse getMyInfo() {
    Long currentUserId = SecurityUtil.getCurrentUserId();

    User user =
        userRepository
            .findById(currentUserId)
            .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    log.info("내 정보 조회 - userId: {}, loginId: {}", user.getId(), user.getLoginId());
    return userMapper.toResponse(user);
  }

  @Override
  public void deleteMyAccount(HttpServletRequest request, HttpServletResponse response) {
    Long currentUserId = SecurityUtil.getCurrentUserId();

    User user =
        userRepository
            .findById(currentUserId)
            .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

    authService.invalidateCurrentSessionQuietly(request, response);

    userRepository.delete(user);
    log.info("회원 탈퇴 완료 - userId: {}, loginId: {}", user.getId(), user.getLoginId());
  }
}
