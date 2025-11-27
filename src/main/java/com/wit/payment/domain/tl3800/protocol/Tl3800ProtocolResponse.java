/*
 * Copyright (c) WIT Global
 */
package com.wit.payment.domain.tl3800.protocol;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Tl3800ProtocolResponse {

  private final String jobCode;          // 실제 응답 job code: "b", "c", "l", "a" 등
  private final String responseCode;     // 예: "0000"
  private final String responseMessage;  // 응답 메시지

  private final String approvalNo;       // 승인번호
  private final LocalDate approvalDate;
  private final LocalTime approvalTime;

  private final BigDecimal amount;       // 승인/취소 금액
  private final String cardNoMasked;     // 마스킹 카드번호
  private final String mediaType;        // IC/RF/QR 등

  private final String rawRequest;       // 전문 원문 (로그용)
  private final String rawResponse;
}