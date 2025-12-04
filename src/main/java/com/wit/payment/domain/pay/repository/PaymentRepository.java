/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.pay.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wit.payment.domain.pay.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

  List<Payment> findAllByOrderByApprovedDateDescApprovedTimeDesc();
}
