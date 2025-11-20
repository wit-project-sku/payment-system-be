/*
 * Copyright (c) WIT Global
 */
package com.wit.payment.domain.product.mapper;

import com.wit.payment.domain.product.dto.request.CreateProductRequest;
import com.wit.payment.domain.product.dto.response.ProductDetailResponse;
import com.wit.payment.domain.product.dto.response.ProductImageResponse;
import com.wit.payment.domain.product.dto.response.ProductSummaryResponse;
import com.wit.payment.domain.product.entity.Product;
import com.wit.payment.domain.product.entity.ProductImage;
import com.wit.payment.domain.product.entity.ProductStatus;
import com.wit.payment.domain.store.entity.Store;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

  public Product toProduct(Store store, CreateProductRequest request) {
    ProductStatus status =
        request.getStatus() != null ? request.getStatus() : ProductStatus.ON_SALE;

    return Product.builder()
        .store(store)
        .name(request.getName())
        .subTitle(request.getSubTitle())
        .price(request.getPrice())
        .description(request.getDescription())
        .status(status)
        .build();
  }

  public List<ProductImage> toProductImages(Product product, List<String> imageUrls) {
    if (imageUrls == null || imageUrls.isEmpty()) {
      return List.of();
    }

    List<ProductImage> images = new ArrayList<>();
    for (int i = 0; i < imageUrls.size(); i++) {
      String url = imageUrls.get(i);
      if (url == null) {
        continue;
      }

      images.add(
          ProductImage.builder()
              .product(product)
              .imageUrl(url)
              .orderNum(i) // 0 = 썸네일, 나머지 = 디테일
              .build());
    }
    return images;
  }

  /**
   * 목록용 요약 응답 (대표 이미지 포함)
   */
  public ProductSummaryResponse toProductSummaryResponse(Product product) {
    String thumbnailUrl =
        product.getImages() == null || product.getImages().isEmpty()
            ? null
            : product.getImages().stream().min(Comparator.comparingInt(ProductImage::getOrderNum))
                .map(ProductImage::getImageUrl)
                .orElse(null);

    return ProductSummaryResponse.builder()
        .id(product.getId())
        .storeName(product.getStore().getName())
        .name(product.getName())
        .subTitle(product.getSubTitle())
        .price(product.getPrice())
        .status(product.getStatus())
        .thumbnailImageUrl(thumbnailUrl)
        .build();
  }

  /**
   * 상세 응답 (모든 이미지 포함)
   */
  public ProductDetailResponse toProductDetailResponse(Product product) {
    List<ProductImage> images =
        product.getImages() == null
            ? List.of()
            : product.getImages().stream()
                .sorted(Comparator.comparingInt(ProductImage::getOrderNum))
                .toList();

    List<ProductImageResponse> imageResponses =
        images.stream().map(this::toProductImageResponse).toList();

    return ProductDetailResponse.builder()
        .id(product.getId())
        .storeName(product.getStore().getName())
        .name(product.getName())
        .subTitle(product.getSubTitle())
        .price(product.getPrice())
        .description(product.getDescription())
        .status(product.getStatus())
        .images(imageResponses)
        .build();
  }

  private ProductImageResponse toProductImageResponse(ProductImage image) {
    return ProductImageResponse.builder()
        .id(image.getId())
        .imageUrl(image.getImageUrl())
        .orderNum(image.getOrderNum())
        .build();
  }
}
