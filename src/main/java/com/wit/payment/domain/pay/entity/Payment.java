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
import jakarta.persistence.FetchType;
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

  @Column(name = "approved_date", nullable = false)
  private LocalDate approvedDate;

  @Column(name = "approved_time", nullable = false)
  private LocalTime approvedTime;

  @Column(name = "approval_no_raw", length = 12, nullable = false)
  private String approvalNoRaw;

  @Column(name = "approval_no", length = 12, nullable = false)
  private String approvalNo;

  @Column(name = "amount", nullable = false)
  private Integer amount;

  @Column(name = "installment", length = 2, nullable = false)
  private String installment;

  @Column(name = "phone_number", length = 20, nullable = false)
  private String phoneNumber;

  @Column(name = "image_url")
  private String imageUrl;

  /** 상세 배송 정보 1:1 (optional) */
  @Setter
  @OneToOne(
      mappedBy = "payment",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private PaymentDelivery delivery;

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
