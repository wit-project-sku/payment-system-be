/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.kiosk.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wit.payment.domain.kiosk.entity.Kiosk;

public interface KioskRepository extends JpaRepository<Kiosk, Long> {

  List<Kiosk> findByIdIn(List<Long> ids);
}
