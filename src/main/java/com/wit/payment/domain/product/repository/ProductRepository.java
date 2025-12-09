/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.product.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.wit.payment.domain.category.entity.Category;
import com.wit.payment.domain.product.entity.Product;
import com.wit.payment.domain.product.entity.ProductStatus;

public interface ProductRepository extends JpaRepository<Product, Long> {

  // storeId 기준으로 HIDDEN 아닌 상품 목록 조회 (키오스크 노출용)
  List<Product> findByCategoryIdAndStatusNotOrderByCreatedAtAsc(Long storeId, ProductStatus status);

  Long deleteByCategoryId(Long categoryId);

  @Query(
      "select distinct p "
          + "from Product p "
          + "join p.kioskProducts kp "
          + "join kp.kiosk k "
          + "where p.category.id = :categoryId "
          + "and p.status <> :status "
          + "and k.id = :kioskId "
          + "order by p.createdAt asc")
  List<Product> findByCategoryAndKioskAndStatusNotOrderByCreatedAtAsc(
      @Param("categoryId") Long categoryId,
      @Param("kioskId") Long kioskId,
      @Param("status") ProductStatus status);

  boolean existsByCategoryAndName(Category category, String name);

  List<Product> findByIdIn(Iterable<Long> ids);

  @Query(
      """
          SELECT pi.imageUrl
          FROM ProductImage pi
          WHERE pi.product.id = :productId
          ORDER BY pi.orderNum ASC
          LIMIT 1
      """)
  String findTopImageUrl(Long productId);
}
