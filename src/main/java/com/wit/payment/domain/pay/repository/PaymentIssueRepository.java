/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.pay.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wit.payment.domain.pay.entity.PaymentIssue;

public interface PaymentIssueRepository extends JpaRepository<PaymentIssue, Long> {

  List<PaymentIssue> findAllByOrderByOccurredDateDescOccurredTimeDesc();
}
