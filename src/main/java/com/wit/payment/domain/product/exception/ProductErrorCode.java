/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.product.exception;

import org.springframework.http.HttpStatus;

import com.wit.payment.global.exception.model.BaseErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductErrorCode implements BaseErrorCode {
  PRODUCT_NOT_FOUND("PRODUCT_4041", "존재하지 않는 상품입니다.", HttpStatus.NOT_FOUND),
  INVALID_PRODUCT_STATUS("PRODUCT_4001", "유효하지 않은 상품 상태입니다.", HttpStatus.BAD_REQUEST);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
