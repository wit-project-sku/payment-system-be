/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.product.entity;

public enum ProductStatus {
  ON_SALE, // 판매 중
  SOLD_OUT, // 품절
  HIDDEN // 숨김(soft delete)
}
