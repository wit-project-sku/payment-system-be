/*
 * Copyright (c) WIT Global
 */
package com.wit.payment.domain.tl3800.dto.request;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Tl3800ApproveRequest {

  @NotNull
  private Long terminalId;

  @NotNull
  private BigDecimal amount;

  private String orderId;

  private boolean useExtended; // true면 JobCode G, false면 B 사용
}