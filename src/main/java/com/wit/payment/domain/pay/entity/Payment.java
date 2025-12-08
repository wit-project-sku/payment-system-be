/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.pay.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import com.wit.payment.global.common.BaseTimeEntity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public class Payment extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "payment_id")
  private Long id;

  // --- 결제 기본 정보 (우리 도메인) ---
  @Column(name = "phone_number", length = 20, nullable = false)
  private String phoneNumber;

  @Column(name = "image_url")
  private String imageUrl;

  @Column(name = "delivery", nullable = false)
  private boolean delivery;

  /** 승인금액(원거래금액 + 세금 + 봉사료) */
  @Column(name = "amount", nullable = false)
  private Integer amount;

  /** 세금(부가세) */
  @Column(name = "vat_amount", nullable = false)
  private Integer vatAmount;

  /** 봉사료 */
  @Column(name = "svc_amount", nullable = false)
  private Integer svcAmount;

  /** 할부개월 (00, 02, 03 ...) */
  @Column(name = "installment", length = 2, nullable = false)
  private String installment;

  // --- 승인/매출 시각 (취소 시 원거래일자/시간에 사용) ---
  /** 매출일자[YYYYMMDD] */
  @Column(name = "org_approved_date", nullable = false)
  private LocalDate orgApprovedDate;

  /** 매출시간[hhmmss] */
  @Column(name = "org_approved_time", nullable = false)
  private LocalTime orgApprovedTime;

  // --- 승인번호 (원문 + 화면표시용) ---
  /** 단말 응답 원문 그대로(좌측 정렬 + space 패딩) */
  @Column(name = "approval_no_raw", length = 12, nullable = false)
  private String approvalNoRaw;

  /** 공백 제거/정규화된 승인번호 */
  @Column(name = "approval_no", length = 12, nullable = false)
  private String approvalNo;

  // --- VAN 거래 식별 정보 (취소/정산용) ---
  /** 거래고유번호(거래날짜 6 + 일련번호 6) */
  @Column(name = "van_transaction_no", length = 12, nullable = false)
  private String vanTransactionNo;

  /** 단말기번호(TID + 일련번호, 14자리) */
  @Column(name = "terminal_no", length = 14, nullable = false)
  private String terminalNo;

  /** 단말기 ID(TID, 헤더 CAT/MID) */
  @Column(name = "terminal_id", length = 16, nullable = false)
  private String terminalId;

  /** 단말 거래일련번호(terminal_no 마지막 4자리 등) */
  @Column(name = "terminal_seq_no", length = 4, nullable = false)
  private String terminalSequenceNo;

  // --- 거래 속성 ---
  /** 거래구분코드(응답전문[b] 첫 바이트) */
  @Column(name = "tran_type_code", length = 1, nullable = false)
  private String tranTypeCode;

  /** 거래매체(IC/MS/RF 등) */
  @Column(name = "media_type", length = 1, nullable = false)
  private String mediaType;

  // --- PG/빌키 등 부가정보 원문 ---
  /** VAN 부가정보 원문(N자리, 필요 시 전체 저장) */
  @Column(name = "van_extra_raw", length = 255)
  private String vanExtraRaw;

  // --- 연관관계 ---
  /** 상세 배송 정보 1:1 (optional) */
  @Setter
  @OneToOne(mappedBy = "payment", cascade = CascadeType.ALL, orphanRemoval = true)
  private PaymentDelivery deliveryDetail;

  /** 결제 상품 항목 목록 */
  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "payment_id", nullable = false)
  @Default
  private List<PaymentItem> items = new ArrayList<>();

  public void addItem(PaymentItem item) {
    if (item == null) {
      return;
    }
    this.items.add(item);
  }
}
