/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.store.exception;

import org.springframework.http.HttpStatus;

import com.wit.payment.global.exception.model.BaseErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StoreErrorCode implements BaseErrorCode {
  STORE_NOT_FOUND("STORE_4041", "존재하지 않는 가게입니다.", HttpStatus.NOT_FOUND);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
