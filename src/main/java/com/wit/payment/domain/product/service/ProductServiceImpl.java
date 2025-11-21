/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.product.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.wit.payment.domain.category.entity.Category;
import com.wit.payment.domain.category.exception.CategoryErrorCode;
import com.wit.payment.domain.category.repository.CategoryRepository;
import com.wit.payment.domain.product.dto.request.CreateProductRequest;
import com.wit.payment.domain.product.dto.request.UpdateProductRequest;
import com.wit.payment.domain.product.dto.response.ProductDetailResponse;
import com.wit.payment.domain.product.dto.response.ProductSummaryResponse;
import com.wit.payment.domain.product.entity.Product;
import com.wit.payment.domain.product.entity.ProductImage;
import com.wit.payment.domain.product.entity.ProductStatus;
import com.wit.payment.domain.product.exception.ProductErrorCode;
import com.wit.payment.domain.product.mapper.ProductMapper;
import com.wit.payment.domain.product.repository.ProductImageRepository;
import com.wit.payment.domain.product.repository.ProductRepository;
import com.wit.payment.global.exception.CustomException;
import com.wit.payment.global.s3.entity.PathName;
import com.wit.payment.global.s3.service.S3Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

  private static final int MAX_IMAGE_COUNT = 4;

  private final CategoryRepository storeRepository;
  private final ProductRepository productRepository;
  private final ProductImageRepository productImageRepository;
  private final ProductMapper productMapper;
  private final S3Service s3Service;

  @Override
  @Transactional
  public ProductDetailResponse createProduct(
      Long categoryId, CreateProductRequest request, List<MultipartFile> images) {

    validateImages(images);

    Category store =
        storeRepository
            .findById(categoryId)
            .orElseThrow(() -> new CustomException(CategoryErrorCode.CATEGORY_NOT_FOUND));

    Product product = productMapper.toProduct(store, request);

    Product saved = productRepository.save(product);

    List<String> imageUrls = toImageUrls(images);

    List<ProductImage> productImages = productMapper.toProductImages(saved, imageUrls);

    if (!productImages.isEmpty()) {
      productImageRepository.saveAll(productImages);
      saved.getImages().addAll(productImages);
    }

    log.info("상품 생성 성공 - productId: {}, storeId: {}", saved.getId(), categoryId);
    return productMapper.toProductDetailResponse(saved);
  }

  @Override
  @Transactional
  public ProductDetailResponse updateProduct(
      Long productId, UpdateProductRequest request, List<MultipartFile> images) {

    Product product =
        productRepository
            .findById(productId)
            .orElseThrow(() -> new CustomException(ProductErrorCode.PRODUCT_NOT_FOUND));

    product.update(
        request.getName(),
        request.getSubTitle(),
        request.getPrice(),
        request.getDescription(),
        request.getStatus());

    if (product.getImages() != null && !product.getImages().isEmpty()) {
      deleteImagesFromS3(product.getImages());
      productImageRepository.deleteAll(product.getImages());
      product.getImages().clear();
    }

    List<String> imageUrls = toImageUrls(images);
    List<ProductImage> newImages = productMapper.toProductImages(product, imageUrls);

    if (!newImages.isEmpty()) {
      productImageRepository.saveAll(newImages);
      product.getImages().addAll(newImages);
    }

    log.info("상품 수정 성공 - productId: {}", productId);
    return productMapper.toProductDetailResponse(product);
  }

  @Override
  @Transactional
  public void softDeleteProduct(Long productId) {
    Product product =
        productRepository
            .findById(productId)
            .orElseThrow(() -> new CustomException(ProductErrorCode.PRODUCT_NOT_FOUND));

    product.hide();
    log.info("상품 soft 삭제(HIDDEN 처리) - productId: {}", productId);
  }

  @Override
  @Transactional
  public void hardDeleteProduct(Long productId) {
    Product product =
        productRepository
            .findById(productId)
            .orElseThrow(() -> new CustomException(ProductErrorCode.PRODUCT_NOT_FOUND));

    if (product.getImages() != null && !product.getImages().isEmpty()) {
      deleteImagesFromS3(product.getImages());
      productImageRepository.deleteAll(product.getImages());
      product.getImages().clear();
    }

    productRepository.delete(product);
    log.info("상품 hard 삭제 - productId: {}", productId);
  }

  @Override
  public List<ProductSummaryResponse> getProductsByCategory(Long categoryId) {
    storeRepository
        .findById(categoryId)
        .orElseThrow(() -> new CustomException(CategoryErrorCode.CATEGORY_NOT_FOUND));

    List<Product> products =
        productRepository.findByCategoryIdAndStatusNotOrderByCreatedAtAsc(
            categoryId, ProductStatus.HIDDEN);

    List<ProductSummaryResponse> responses =
        products.stream().map(productMapper::toProductSummaryResponse).toList();

    log.info("가게별 상품 목록 조회 성공 - storeId: {}, count: {}", categoryId, responses.size());
    return responses;
  }

  @Override
  public ProductDetailResponse getProductDetail(Long productId) {
    Product product =
        productRepository
            .findById(productId)
            .orElseThrow(() -> new CustomException(ProductErrorCode.PRODUCT_NOT_FOUND));

    log.info("상품 상세 조회 성공 - productId: {}", productId);
    return productMapper.toProductDetailResponse(product);
  }

  /**
   * MultipartFile 리스트를 S3에 업로드하고, 업로드된 파일의 URL 리스트를 반환합니다.
   *
   * <p>각 파일은 {@link PathName#PRODUCT} 경로에 업로드됩니다.
   */
  private List<String> toImageUrls(List<MultipartFile> files) {
    if (files == null || files.isEmpty()) {
      return List.of();
    }
    List<String> urls = new ArrayList<>();
    for (MultipartFile file : files) {
      if (file == null || file.isEmpty()) {
        continue;
      }
      // S3에 업로드하고 URL 추출
      String imageUrl = s3Service.uploadImage(PathName.PRODUCT, file).getImageUrl();
      urls.add(imageUrl);
    }
    return urls;
  }

  /**
   * ProductImage 엔티티 리스트에 포함된 이미지들을 S3에서 삭제합니다.
   *
   * @param images 삭제할 이미지 엔티티 리스트
   */
  private void deleteImagesFromS3(List<ProductImage> images) {
    for (ProductImage image : images) {
      if (image.getImageUrl() == null) {
        continue;
      }
      s3Service.deleteByUrl(image.getImageUrl());
    }
  }

  /** 상품 생성 시 이미지가 최소 1장 이상 최대 4장 이하로 존재하는지 검증합니다. */
  private void validateImages(List<MultipartFile> images) {

    if (images == null || images.isEmpty()) {
      throw new CustomException(ProductErrorCode.IMAGE_REQUIRED);
    }

    long nonEmptyCount = images.stream().filter(file -> file != null && !file.isEmpty()).count();

    if (nonEmptyCount == 0) {
      throw new CustomException(ProductErrorCode.IMAGE_REQUIRED);
    }

    if (nonEmptyCount > MAX_IMAGE_COUNT) {
      throw new CustomException(ProductErrorCode.TOO_MANY_IMAGES);
    }
  }
}
