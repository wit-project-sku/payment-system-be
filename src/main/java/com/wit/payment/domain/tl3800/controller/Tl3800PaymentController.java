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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface Tl3800PaymentController {

  ResponseEntity<Tl3800ApproveResponse> approve(@RequestBody Tl3800ApproveRequest request);

  ResponseEntity<Tl3800CancelResponse> cancel(@RequestBody Tl3800CancelRequest request);

  ResponseEntity<Tl3800LastApprovalResponse> lastApproval(
      @RequestBody Tl3800LastApprovalRequest request
  );

  ResponseEntity<Tl3800StatusResponse> status(@RequestBody Tl3800StatusCheckRequest request);
}