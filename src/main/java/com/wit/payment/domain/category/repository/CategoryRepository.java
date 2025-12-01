/*
 * Copyright (c) WIT Global
 */
package com.wit.payment.domain.category.repository;

import com.wit.payment.domain.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

  boolean existsByName(String name);
}
