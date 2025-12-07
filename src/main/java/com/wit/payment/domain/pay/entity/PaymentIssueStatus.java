/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.pay.entity;

public enum PaymentIssueStatus {
  UNRESOLVED, // 미처리 (기본값)
  IN_PROGRESS, // 처리중
  RESOLVED // 처리완료
}
