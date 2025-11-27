/*
 * Copyright (c) WIT Global
 */
package com.wit.payment.domain.tl3800.exception;

import com.wit.payment.global.exception.model.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum Tl3800ErrorCode implements BaseErrorCode {

  TERMINAL_NOT_FOUND("TL3800_4041", "등록된 TL3800 단말기를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

  PROTOCOL_TIMEOUT("TL3800_4081", "TL3800 단말기 응답 시간이 초과되었습니다.", HttpStatus.REQUEST_TIMEOUT),
  PROTOCOL_IO_ERROR("TL3800_5001", "TL3800 단말 통신 중 I/O 오류가 발생했습니다.",
      HttpStatus.INTERNAL_SERVER_ERROR),

  APPROVAL_FAILED("TL3800_4001", "승인 요청에 실패했습니다.", HttpStatus.BAD_REQUEST),
  CANCEL_FAILED("TL3800_4002", "취소 요청에 실패했습니다.", HttpStatus.BAD_REQUEST),

  LAST_APPROVAL_NOT_FOUND("TL3800_4042", "단말기 또는 DB에서 마지막 승인내역을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

  STATUS_CHECK_FAILED("TL3800_4003", "단말기 상태 조회에 실패했습니다.", HttpStatus.BAD_REQUEST);

  private final String code;
  private final String message;
  private final HttpStatus status;
}