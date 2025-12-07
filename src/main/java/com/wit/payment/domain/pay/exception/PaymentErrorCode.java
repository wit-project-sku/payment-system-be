/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.pay.exception;

import org.springframework.http.HttpStatus;

import com.wit.payment.global.exception.model.BaseErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentErrorCode implements BaseErrorCode {
  PRODUCT_NOT_FOUND("PAY_4041", "존재하지 않는 상품이 포함되어 있습니다.", HttpStatus.NOT_FOUND),
  PAYMENT_NOT_FOUND("PAY_4042", "결제 내역이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
  PAYMENT_ITEM_NOT_FOUND("PAY_4043", "결제 상품이 존재하지 않습니다.", HttpStatus.NOT_FOUND),

  PRODUCT_STATUS_INVALID("PAY_4001", "결제할 수 없는 상품 상태입니다.", HttpStatus.BAD_REQUEST),
  AMOUNT_MISMATCH("PAY_4002", "요청 금액과 서버 계산 금액이 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
  EMPTY_ITEMS("PAY_4003", "빈 상품으로 결제를 요청했습니다.", HttpStatus.BAD_REQUEST),

  TERMINAL_REJECTED("PAY_4100", "단말에서 결제가 거절되었습니다.", HttpStatus.BAD_REQUEST),
  TERMINAL_TIMEOUT("PAY_5041", "단말 응답이 지연되었습니다.", HttpStatus.GATEWAY_TIMEOUT),
  TERMINAL_ERROR("PAY_5001", "단말 통신 중 오류가 발생했습니다.", HttpStatus.BAD_GATEWAY);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
