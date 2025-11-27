/*
 * Copyright (c) WIT Global
 */
package com.wit.payment.domain.tl3800.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Tl3800StatusCheckRequest {

  @NotNull
  private Long terminalId;
}