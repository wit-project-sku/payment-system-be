/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.pay.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "payment_issues")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public class PaymentIssue {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "payment_issue_id")
  private Long id;

  /** 이슈 발생 일자(YYYY-MM-DD) */
  @Column(name = "occurred_date", nullable = false)
  private LocalDate occurredDate;

  /** 이슈 발생 시각(hh:mm:ss) */
  @Column(name = "occurred_time", nullable = false)
  private LocalTime occurredTime;

  /** 이슈 당시 결제금액(원) */
  @Column(name = "amount", nullable = false)
  private Integer amount;

  /** 이슈 내용 – 잔액 부족, 단말기 오류, 타임아웃 등 */
  @Column(name = "message", length = 1000, nullable = false)
  private String message;

  /** 고객 전화번호 */
  @Column(name = "phone_number", length = 20, nullable = false)
  private String phoneNumber;

  /** 처리상태 – 미처리(기본), 처리중, 처리완료 */
  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 20, nullable = false)
  @Default
  private PaymentIssueStatus status = PaymentIssueStatus.UNRESOLVED;

  // 상태 변경용 도메인 메서드
  public void markInProgress() {
    this.status = PaymentIssueStatus.IN_PROGRESS;
  }

  public void markResolved() {
    this.status = PaymentIssueStatus.RESOLVED;
  }
}
