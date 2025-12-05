/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.pay.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wit.payment.domain.pay.entity.Payment;
import com.wit.payment.domain.pay.entity.PaymentDelivery;

public interface PaymentDeliveryRepository extends JpaRepository<PaymentDelivery, Long> {

  Optional<PaymentDelivery> findByPayment(Payment payment);
}
