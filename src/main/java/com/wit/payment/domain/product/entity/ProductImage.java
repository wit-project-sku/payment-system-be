/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.product.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.wit.payment.global.common.BaseTimeEntity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_image")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ProductImage extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  @Column(name = "image_url", nullable = false)
  private String imageUrl;

  /** 대표 이미지 순서(0,1,2...). 리스트 조회시 order ASC로 정렬해서 첫 번째 이미지를 대표 이미지로 사용. */
  @Column(name = "image_order", nullable = false)
  private Integer orderNum;

  public void updateOrder(Integer orderNum) {
    this.orderNum = orderNum;
  }
}
