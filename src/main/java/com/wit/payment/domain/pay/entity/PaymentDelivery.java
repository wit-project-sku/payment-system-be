/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.pay.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import com.wit.payment.global.common.BaseTimeEntity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@Table(name = "payment_deliveries")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public class PaymentDelivery extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "payment_delivery_id")
  private Long id;

  /** 어떤 결제의 배송 정보인지 (FK) */
  @Setter
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "payment_id", nullable = false, unique = true)
  private Payment payment;

  @Column(name = "recipient_name", length = 50, nullable = false)
  private String name;

  @Column(name = "zip_code", length = 10, nullable = false)
  private String zipCode;

  @Column(name = "address", length = 255, nullable = false)
  private String address;

  @Column(name = "detail_address", length = 255, nullable = false)
  private String detailAddress;

  public void update(String name, String zipCode, String address, String detailAddress) {
    this.name = name;
    this.zipCode = zipCode;
    this.address = address;
    this.detailAddress = detailAddress;
  }

  public String fullAddress() {
    return address + " " + detailAddress;
  }
}
