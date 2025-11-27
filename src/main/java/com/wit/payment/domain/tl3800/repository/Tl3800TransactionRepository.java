/*
 * Copyright (c) WIT Global
 */
package com.wit.payment.domain.tl3800.repository;

import com.wit.payment.domain.tl3800.entity.Tl3800Terminal;
import com.wit.payment.domain.tl3800.entity.Tl3800Transaction;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Tl3800TransactionRepository extends JpaRepository<Tl3800Transaction, Long> {

  Optional<Tl3800Transaction> findTopByTerminalOrderByApprovedAtDesc(Tl3800Terminal terminal);
}