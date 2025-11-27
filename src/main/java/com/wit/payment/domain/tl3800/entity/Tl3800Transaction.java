/*
 * Copyright (c) WIT Global
 */
package com.wit.payment.domain.tl3800.entity;

import com.wit.payment.domain.tl3800.protocol.Tl3800JobCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tl3800_transaction")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Tl3800Transaction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "terminal_id", nullable = false)
  private Tl3800Terminal terminal;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 10)
  private Tl3800JobCode jobCode;

  @Column(nullable = false, precision = 13, scale = 2)
  private BigDecimal amount;

  @Column(length = 40)
  private String orderId;

  @Column(length = 40)
  private String approvalNo;

  @Column(nullable = false)
  private LocalDateTime approvedAt;

  @Column(nullable = false, length = 20)
  private String responseCode;

  @Column(nullable = false, length = 200)
  private String responseMessage;

  @Column(length = 100)
  private String cardNoMasked;

  @Column(length = 50)
  private String mediaType; // IC/RF/QR 등

  @Column(length = 2000)
  private String rawRequest;

  @Column(length = 2000)
  private String rawResponse;
}