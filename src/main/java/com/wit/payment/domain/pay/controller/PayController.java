/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.pay.controller;

import jakarta.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wit.payment.domain.pay.dto.request.PayRequest;
import com.wit.payment.domain.pay.dto.response.PayResponse;
import com.wit.payment.domain.pay.service.PayService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Pay", description = "상품 결제/이슈 관리 API")
@RestController
@RequestMapping(value = "/api/pay", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PayController {

  private final PayService payService;

  @Operation(
      summary = "장바구니 결제 요청",
      description = "상품 ID/수량/총 금액/할부/배송 여부를 기반으로 단말 승인 및 결제/이슈를 생성합니다.")
  @PostMapping
  public PayResponse pay(@Valid @RequestBody PayRequest request) {
    return payService.pay(request);
  }
}
