/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.pay.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.wit.payment.global.common.BaseTimeEntity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "payments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public class Payment extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "payment_id")
  private Long id;

  /** 승인일자(YYYY-MM-DD) – TL 패킷의 dateTime14(YYYYMMDDhhmmss)에서 날짜 부분 파싱 */
  @Column(name = "approved_date", nullable = false)
  private LocalDate approvedDate;

  /** 승인시각(hh:mm:ss) – TL 패킷의 dateTime14에서 시간 부분 파싱 */
  @Column(name = "approved_time", nullable = false)
  private LocalTime approvedTime;

  /** 승인번호 Raw – TL 응답 data 영역의 승인번호 12바이트를 공백 포함 그대로 저장 취소요청 시에는 이 값을 그대로 사용해야 함 */
  @Column(name = "approval_no_raw", length = 12, nullable = false)
  private String approvalNoRaw;

  /** 승인번호 표시용 – approvalNoRaw.trim() 결과 UI/영수증 등 사람이 보는 용도 */
  @Column(name = "approval_no", length = 12, nullable = false)
  private String approvalNo;

  /** 승인금액(원) – TL 응답 승인금액 또는 비즈니스 로직에서 검증된 금액 */
  @Column(name = "amount", nullable = false)
  private Integer amount;

  /** 할부개월(“00”, “02”, “03” …) */
  @Column(name = "installment", length = 2, nullable = false)
  private String installment;

  /** 고객 전화번호 – 이후 별도 API로 채워질 예정 (null 가능) */
  @Column(name = "phone_number", length = 20)
  private String phoneNumber;

  /** 배송지 주소 – 배송이 아닌 결제는 null */
  @Column(name = "delivery_address", length = 255)
  private String deliveryAddress;

  /** 기타 메모/비고 – 배송이 아닌 결제는 null */
  @Column(name = "etc", length = 500)
  private String etc;

  public void updateEtc(String phoneNumber, String deliveryAddress, String etc) {
    this.phoneNumber = phoneNumber;
    this.deliveryAddress = deliveryAddress;
    this.etc = etc;
  }

  public void updateContact(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }
}
