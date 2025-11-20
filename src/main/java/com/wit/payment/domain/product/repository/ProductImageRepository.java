/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.product.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wit.payment.domain.product.entity.ProductImage;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

  List<ProductImage> findByProductIdOrderByOrderNumAsc(Long productId);
}
