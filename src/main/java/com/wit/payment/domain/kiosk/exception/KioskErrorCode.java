/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.kiosk.exception;

import org.springframework.http.HttpStatus;

import com.wit.payment.global.exception.model.BaseErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum KioskErrorCode implements BaseErrorCode {
  KIOSK_ALREADY_EXISTS("KIOSK_4001", "이미 존재하는 키오스크입니다.", HttpStatus.BAD_REQUEST),

  KIOSK_NOT_FOUND("KIOSK_4041", "존재하지 않는 키오스크입니다.", HttpStatus.NOT_FOUND);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
