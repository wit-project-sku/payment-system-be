/*
 * Copyright (c) WIT Global
 */
package com.wit.payment.domain.tl3800.protocol;

public enum Tl3800JobCode {
  B_APPROVAL,      // Job Code "B" 기본 승인
  G_APPROVAL,      // Job Code "G" 부가정보 승인
  C_CANCEL,        // Job Code "C" 취소
  L_LAST_APPROVAL, // Job Code "L" 마지막 승인 조회
  A_STATUS         // Job Code "A" 단말 상태 조회
}