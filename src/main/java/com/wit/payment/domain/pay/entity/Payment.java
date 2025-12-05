/*
 * Copyright (c) WIT Global
 */
package com.wit.payment.domain.pay.entity;

import com.wit.payment.global.common.BaseTimeEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
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

  /**
   * 승인일자(YYYY-MM-DD) – TL 패킷의 dateTime14(YYYYMMDDhhmmss)에서 날짜 부분 파싱
   */
  @Column(name = "approved_date", nullable = false)
  private LocalDate approvedDate;

  /**
   * 승인시각(hh:mm:ss) – TL 패킷의 dateTime14에서 시간 부분 파싱
   */
  @Column(name = "approved_time", nullable = false)
  private LocalTime approvedTime;

  /**
   * 승인번호 Raw – TL 응답 data 영역의 승인번호 12바이트를 공백 포함 그대로 저장
   */
  @Column(name = "approval_no_raw", length = 12, nullable = false)
  private String approvalNoRaw;

  /**
   * 승인번호 표시용 – approvalNoRaw.trim() 결과
   */
  @Column(name = "approval_no", length = 12, nullable = false)
  private String approvalNo;

  /**
   * 승인금액(원)
   */
  @Column(name = "amount", nullable = false)
  private Integer amount;

  /**
   * 할부개월(“00”, “02”, “03” …)
   */
  @Column(name = "installment", length = 2, nullable = false)
  private String installment;

  /**
   * 고객 전화번호 (필수)
   */
  @Column(name = "phone_number", length = 20, nullable = false)
  private String phoneNumber;

  /**
   * 커스텀 굿즈인 경우 필요한 이미지 url
   */
  @Column(name = "image_url")
  private String imageUrl;

  /**
   * 배송지 주소 – 배송이 아닌 결제는 null (요약 주소, 풀 주소는 Delivery 엔티티에 저장)
   */
  @Column(name = "delivery_address", length = 255)
  private String deliveryAddress;

  /**
   * 결제에 포함된 상품들(상품 ID + 옵션 텍스트)
   */
  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @Default
  private List<PaymentItem> items = new ArrayList<>();

  /**
   * 배송지 요약 주소 업데이트
   */
  public void updateAddress(String deliveryAddress) {
    this.deliveryAddress = deliveryAddress;
  }

  /**
   * 결제 상품 추가 (양방향이 아니므로 payment 세팅은 없음)
   */
  public void addItem(PaymentItem item) {
    if (item == null) {
      return;
    }
    this.items.add(item);
  }
}