/*
 * Copyright (c) WIT Global
 */
package com.wit.payment.domain.tl3800.protocol;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public interface Tl3800ProtocolClient {

  /**
   * VAN 신용 승인 (Job Code B 또는 G)
   */
  Tl3800ProtocolResponse requestApproval(
      String ip,
      int port,
      BigDecimal amount,
      String orderId,
      String tid,           // ← VAN TID (CAT ID)
      boolean useExtended   // true: G승인, false: B승인
  );

  /**
   * VAN 신용 취소 (Job Code C)
   */
  Tl3800ProtocolResponse requestCancel(
      String ip,
      int port,
      BigDecimal amount,
      String originalApprovalNo,
      LocalDate originalApprovalDate,
      LocalTime originalApprovalTime,
      String tid             // ← VAN TID (CAT ID)
  );

  /**
   * 단말기에 저장된 마지막 승인 조회 (Job Code L)
   */
  Tl3800ProtocolResponse requestLastApproval(
      String ip,
      int port,
      String tid
  );

  /**
   * 단말기 상태 조회 (Job Code A)
   */
  Tl3800ProtocolResponse requestStatus(String ip, int port);
}