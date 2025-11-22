/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.user.exception;

import org.springframework.http.HttpStatus;

import com.wit.payment.global.exception.model.BaseErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements BaseErrorCode {
  DUPLICATE_NICKNAME("USER_4001", "이미 사용 중인 닉네임입니다.", HttpStatus.BAD_REQUEST),
  DUPLICATE_EMAIL("USER_4002", "이미 사용 중인 이메일입니다.", HttpStatus.BAD_REQUEST),
  USER_NOT_FOUND("USER_4041", "회원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  DUPLICATE_LOGIN_ID("USER_4091", "이미 사용 중인 로그인 아이디입니다.", HttpStatus.CONFLICT),

  IMAGE_UPLOAD_FAILED("USER_5001", "프로필 이미지 업로드에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  USER_SAVE_FAILED("USER_5002", "회원 정보 저장에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  USER_DELETE_FAILED("USER_5003", "사용자 삭제에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  ;

  private final String code;
  private final String message;
  private final HttpStatus status;
}
