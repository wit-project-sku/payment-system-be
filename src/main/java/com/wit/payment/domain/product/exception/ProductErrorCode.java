/*
 * Copyright (c) WIT Global
 */
package com.wit.payment.domain.product.exception;

import com.wit.payment.global.exception.model.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ProductErrorCode implements BaseErrorCode {
  PRODUCT_NOT_FOUND("PRODUCT_4041", "존재하지 않는 상품입니다.", HttpStatus.NOT_FOUND),
  INVALID_PRODUCT_STATUS("PRODUCT_4001", "유효하지 않은 상품 상태입니다.", HttpStatus.BAD_REQUEST),

  IMAGE_REQUIRED("PRODUCT_4002", "상품 이미지는 최소 1장 이상 등록해야 합니다.", HttpStatus.BAD_REQUEST),
  TOO_MANY_IMAGES("PRODUCT_4003", "상품 이미지는 최대 4장까지만 등록 가능합니다.", HttpStatus.BAD_REQUEST),

  PRODUCT_ALREADY_EXISTS("PRODUCT_4004", "이미 동일한 이름의 상품이 존재합니다.", HttpStatus.BAD_REQUEST);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
