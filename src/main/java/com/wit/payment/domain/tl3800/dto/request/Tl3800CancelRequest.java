/*
 * Copyright (c) WIT Global
 */
package com.wit.payment.domain.tl3800.dto.request;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Tl3800CancelRequest {

  @NotNull
  private Long terminalId;

  @NotNull
  private BigDecimal amount;

  @NotNull
  private String originalApprovalNo;

  @NotNull
  private LocalDate originalApprovalDate;

  @NotNull
  private LocalTime originalApprovalTime;
}