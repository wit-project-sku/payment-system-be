/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.category.exception;

import org.springframework.http.HttpStatus;

import com.wit.payment.global.exception.model.BaseErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CategoryErrorCode implements BaseErrorCode {
  CATEGORY_ALREADY_EXISTS("CATEGORY_4001", "이미 존재하는 카테고리입니다.", HttpStatus.BAD_REQUEST),

  CATEGORY_NOT_FOUND("CATEGORY_4041", "존재하지 않는 카테고리입니다.", HttpStatus.NOT_FOUND);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
