/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wit.payment.domain.category.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

  boolean existsByName(String name);
}
