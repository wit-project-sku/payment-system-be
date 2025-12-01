/*
 * Copyright (c) WIT Global
 */
package com.wit.payment.domain.kiosk.repository;

import com.wit.payment.domain.kiosk.entity.Kiosk;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KioskRepository extends JpaRepository<Kiosk, Long> {

  List<Kiosk> findByIdIn(List<Long> ids);

  boolean existsByName(String name);
}
