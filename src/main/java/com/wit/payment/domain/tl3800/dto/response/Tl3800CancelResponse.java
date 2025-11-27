/*
 * Copyright (c) WIT Global
 */
package com.wit.payment.domain.tl3800.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Tl3800CancelResponse {

  private final Long transactionId;
  private final String approvalNo;
  private final BigDecimal amount;
  private final String responseCode;
  private final String responseMessage;
  private final LocalDateTime canceledAt;
}