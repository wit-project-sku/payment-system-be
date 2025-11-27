/*
 * Copyright (c) WIT Global
 */
package com.wit.payment.domain.tl3800.service;

import com.wit.payment.domain.tl3800.dto.request.Tl3800ApproveRequest;
import com.wit.payment.domain.tl3800.dto.request.Tl3800CancelRequest;
import com.wit.payment.domain.tl3800.dto.request.Tl3800LastApprovalRequest;
import com.wit.payment.domain.tl3800.dto.request.Tl3800StatusCheckRequest;
import com.wit.payment.domain.tl3800.dto.response.Tl3800ApproveResponse;
import com.wit.payment.domain.tl3800.dto.response.Tl3800CancelResponse;
import com.wit.payment.domain.tl3800.dto.response.Tl3800LastApprovalResponse;
import com.wit.payment.domain.tl3800.dto.response.Tl3800StatusResponse;

public interface Tl3800PaymentService {

  Tl3800ApproveResponse approve(Tl3800ApproveRequest request);

  Tl3800CancelResponse cancel(Tl3800CancelRequest request);

  Tl3800LastApprovalResponse findLastApproval(Tl3800LastApprovalRequest request);

  Tl3800StatusResponse checkStatus(Tl3800StatusCheckRequest request);
}