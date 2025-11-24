/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.product.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.wit.payment.domain.category.entity.Category;
import com.wit.payment.domain.kiosk.entity.Kiosk;
import com.wit.payment.domain.kiosk.entity.KioskProduct;
import com.wit.payment.global.common.BaseTimeEntity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Product extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id", nullable = false)
  private Category category;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "sub_title")
  private String subTitle;

  @Column(name = "price", nullable = false)
  private Integer price;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  @Builder.Default
  private ProductStatus status = ProductStatus.ON_SALE;

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<ProductImage> images = new ArrayList<>();

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<KioskProduct> kioskProducts = new ArrayList<>();

  public void update(
      String name, String subTitle, Integer price, String description, ProductStatus status) {
    this.name = name;
    this.subTitle = subTitle;
    this.price = price;
    this.description = description;
    this.status = status;
  }

  public void hide() {
    this.status = ProductStatus.HIDDEN;
  }

  public void updateKiosks(List<Kiosk> kiosks) {
    for (KioskProduct kp : this.kioskProducts) {
      kp.getKiosk().getKioskProducts().remove(kp);
    }
    this.kioskProducts.clear();

    if (kiosks == null || kiosks.isEmpty()) {
      return;
    }

    for (Kiosk kiosk : kiosks) {
      KioskProduct kp = KioskProduct.of(kiosk, this);
      this.kioskProducts.add(kp);
      kiosk.getKioskProducts().add(kp);
    }
  }
}
