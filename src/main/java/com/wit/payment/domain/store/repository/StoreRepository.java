/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wit.payment.domain.store.entity.Store;

public interface StoreRepository extends JpaRepository<Store, Long> {}
