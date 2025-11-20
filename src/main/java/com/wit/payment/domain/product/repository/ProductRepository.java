/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.product.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wit.payment.domain.product.entity.Product;
import com.wit.payment.domain.product.entity.ProductStatus;

public interface ProductRepository extends JpaRepository<Product, Long> {

  // storeId 기준으로 HIDDEN 아닌 상품 목록 조회 (키오스크 노출용)
  List<Product> findByStoreIdAndStatusNotOrderByCreatedAtAsc(Long storeId, ProductStatus status);
}
