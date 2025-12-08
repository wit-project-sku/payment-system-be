/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.pay.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wit.payment.domain.pay.dto.request.PayFailureReportRequest;
import com.wit.payment.domain.pay.dto.request.PaySuccessReportRequest;
import com.wit.payment.domain.pay.service.PayReportService;
import com.wit.payment.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "PayReport", description = "로컬 결제 보고 수신 관련 API")
@RestController
@RequestMapping(value = "/api/pay", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PayReportController {

  private final PayReportService payReportService;

  @Operation(
      summary = "로컬 결제 성공 보고 수신 API",
      description = "키오스크 로컬 에이전트에서 TL3800 승인 결과를 보고하면 중앙 서버에서 Payment를 생성합니다.")
  @PostMapping("/success")
  public ResponseEntity<BaseResponse<Void>> reportSuccess(
      @Valid @RequestBody PaySuccessReportRequest request) {

    payReportService.reportSuccess(request);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(BaseResponse.success("결제 성공 보고가 처리되었습니다.", null));
  }

  @Operation(
      summary = "로컬 결제 실패/예외 보고 수신 API",
      description = "키오스크 로컬 에이전트에서 결제 실패/예외 정보를 보고하면 중앙 서버에서 PaymentIssue를 생성합니다.")
  @PostMapping("/failure")
  public ResponseEntity<BaseResponse<Void>> reportFailure(
      @Valid @RequestBody PayFailureReportRequest request) {

    payReportService.reportFailure(request);

    return ResponseEntity.ok(BaseResponse.success("결제 실패/예외 보고가 처리되었습니다.", null));
  }
}
