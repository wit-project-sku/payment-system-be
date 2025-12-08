/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.pay.controller;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wit.payment.domain.pay.dto.request.PaymentOptionAndDeliveryRequest;
import com.wit.payment.domain.pay.dto.response.PaymentIssueResponse;
import com.wit.payment.domain.pay.dto.response.PaymentSummaryResponse;
import com.wit.payment.domain.pay.dto.response.PaymentWithItemsResponse;
import com.wit.payment.domain.pay.service.PayService;
import com.wit.payment.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Pay", description = "상품 결제/이슈 관리 API")
@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PayController {

  private final PayService payService;

  @GetMapping("/admin/payments")
  @Operation(summary = "결제 내역 전체 조회 API", description = "승인된 결제 내역을 최신순으로 전체 조회합니다.")
  public ResponseEntity<BaseResponse<List<PaymentSummaryResponse>>> getAllPayments() {

    List<PaymentSummaryResponse> responses = payService.getAllPayments();

    return ResponseEntity.status(HttpStatus.OK)
        .body(BaseResponse.success("결제 내역 조회가 완료되었습니다.", responses));
  }

  @GetMapping("/admin/issues")
  @Operation(summary = "결제 이슈 내역 전체 조회 API", description = "결제 실패/예외 이슈 내역을 최신순으로 전체 조회합니다.")
  public ResponseEntity<BaseResponse<List<PaymentIssueResponse>>> getAllPaymentIssues() {

    List<PaymentIssueResponse> responses = payService.getAllPaymentIssues();

    return ResponseEntity.status(HttpStatus.OK)
        .body(BaseResponse.success("결제 이슈 내역 조회가 완료되었습니다.", responses));
  }

  @Operation(
      summary = "결제 옵션 + 배송 정보 저장 API",
      description = "결제 후 상품별 옵션(기종 등)과 배송 정보를 한 번에 저장합니다. " + "전화번호는 URL이 아닌 요청 바디로 전달합니다.")
  @PostMapping("/pay/options")
  public ResponseEntity<BaseResponse<Void>> saveOptionsAndDelivery(
      @Valid @RequestBody PaymentOptionAndDeliveryRequest request) {

    payService.saveOptionsAndDelivery(request);

    return ResponseEntity.ok(BaseResponse.success("결제 옵션 및 배송 정보 저장이 완료되었습니다.", null));
  }

  @Operation(summary = "전화번호 기준 구매 이력 조회 API", description = "전화번호로 고객의 결제 내역과 상품 목록을 조회합니다.")
  @GetMapping("/pay/phone")
  public ResponseEntity<BaseResponse<List<PaymentWithItemsResponse>>> getPaymentsByPhone(
      @RequestParam("phoneNumber") @NotBlank String phoneNumber) {

    List<PaymentWithItemsResponse> responses = payService.getPaymentsByPhone(phoneNumber);

    return ResponseEntity.ok(BaseResponse.success("구매 내역 조회가 완료되었습니다.", responses));
  }
}
