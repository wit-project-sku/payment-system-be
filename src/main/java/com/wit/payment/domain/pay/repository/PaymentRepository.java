/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.pay.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wit.payment.domain.pay.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

  // 승인(매출)일시 기준 전체 조회
  List<Payment> findAllByOrderByOrgApprovedDateDescOrgApprovedTimeDesc();

  // 전화번호 + 승인(매출)일시 기준 조회
  List<Payment> findByPhoneNumberOrderByOrgApprovedDateDescOrgApprovedTimeDesc(String phoneNumber);

  // 전화번호 기준 가장 최근 승인 건
  Optional<Payment> findTopByPhoneNumberOrderByOrgApprovedDateDescOrgApprovedTimeDesc(
      String phoneNumber);
}
