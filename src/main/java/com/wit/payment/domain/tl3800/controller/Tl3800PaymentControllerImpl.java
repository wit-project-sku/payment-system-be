/*
 * Copyright (c) WIT Global
 */
package com.wit.payment.domain.tl3800.controller;

import com.wit.payment.domain.tl3800.dto.request.Tl3800ApproveRequest;
import com.wit.payment.domain.tl3800.dto.request.Tl3800CancelRequest;
import com.wit.payment.domain.tl3800.dto.request.Tl3800LastApprovalRequest;
import com.wit.payment.domain.tl3800.dto.request.Tl3800StatusCheckRequest;
import com.wit.payment.domain.tl3800.dto.response.Tl3800ApproveResponse;
import com.wit.payment.domain.tl3800.dto.response.Tl3800CancelResponse;
import com.wit.payment.domain.tl3800.dto.response.Tl3800LastApprovalResponse;
import com.wit.payment.domain.tl3800.dto.response.Tl3800StatusResponse;
import com.wit.payment.domain.tl3800.service.Tl3800PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tl3800")
@RequiredArgsConstructor
public class Tl3800PaymentControllerImpl implements Tl3800PaymentController {

  private final Tl3800PaymentService tl3800PaymentService;

  @Override
  @PostMapping("/payments/approve")
  public ResponseEntity<Tl3800ApproveResponse> approve(
      @Valid @RequestBody Tl3800ApproveRequest request
  ) {
    Tl3800ApproveResponse response = tl3800PaymentService.approve(request);
    return ResponseEntity.ok(response);
  }

  @Override
  @PostMapping("/payments/cancel")
  public ResponseEntity<Tl3800CancelResponse> cancel(
      @Valid @RequestBody Tl3800CancelRequest request
  ) {
    Tl3800CancelResponse response = tl3800PaymentService.cancel(request);
    return ResponseEntity.ok(response);
  }

  @Override
  @PostMapping("/payments/last-approval")
  public ResponseEntity<Tl3800LastApprovalResponse> lastApproval(
      @Valid @RequestBody Tl3800LastApprovalRequest request
  ) {
    Tl3800LastApprovalResponse response = tl3800PaymentService.findLastApproval(request);
    return ResponseEntity.ok(response);
  }

  @Override
  @PostMapping("/terminals/status")
  public ResponseEntity<Tl3800StatusResponse> status(
      @Valid @RequestBody Tl3800StatusCheckRequest request
  ) {
    Tl3800StatusResponse response = tl3800PaymentService.checkStatus(request);
    return ResponseEntity.ok(response);
  }
}