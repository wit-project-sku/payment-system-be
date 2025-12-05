/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.pay.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.wit.payment.domain.pay.entity.PaymentIssue;
import com.wit.payment.domain.pay.mapper.PaymentMapper;
import com.wit.payment.domain.pay.repository.PaymentIssueRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentIssueService {

  private final PaymentIssueRepository paymentIssueRepository;
  private final PaymentMapper paymentMapper;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public PaymentIssue saveIssue(long amount, String message, String phoneNumber) {
    PaymentIssue issue = paymentMapper.toIssue(amount, message, phoneNumber);
    return paymentIssueRepository.save(issue);
  }
}
