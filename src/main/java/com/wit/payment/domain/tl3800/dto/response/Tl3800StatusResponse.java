/*
 * Copyright (c) WIT Global
 */
package com.wit.payment.domain.tl3800.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Tl3800StatusResponse {

  private final String responseCode;
  private final String responseMessage;
}