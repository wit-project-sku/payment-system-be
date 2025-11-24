/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.product.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.wit.payment.domain.product.dto.request.CreateProductRequest;
import com.wit.payment.domain.product.dto.request.UpdateProductRequest;
import com.wit.payment.domain.product.dto.response.ProductDetailResponse;
import com.wit.payment.domain.product.dto.response.ProductSummaryResponse;
import com.wit.payment.domain.product.service.ProductService;
import com.wit.payment.global.response.BaseResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ProductControllerImpl implements ProductController {

  private final ProductService productService;

  @Override
  public ResponseEntity<BaseResponse<ProductDetailResponse>> createProduct(
      @PathVariable("category-id") Long categoryId,
      @Valid @RequestPart("data") CreateProductRequest request,
      @RequestPart(value = "images", required = false) List<MultipartFile> images) {

    ProductDetailResponse response = productService.createProduct(categoryId, request, images);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(BaseResponse.success("상품 생성이 완료되었습니다.", response));
  }

  @Override
  public ResponseEntity<BaseResponse<ProductDetailResponse>> updateProduct(
      @PathVariable("product-id") Long productId,
      @Valid @RequestPart("data") UpdateProductRequest request,
      @RequestPart(value = "images", required = false) List<MultipartFile> images) {

    ProductDetailResponse response = productService.updateProduct(productId, request, images);

    return ResponseEntity.status(HttpStatus.OK)
        .body(BaseResponse.success("상품 수정이 완료되었습니다.", response));
  }

  @Override
  public ResponseEntity<BaseResponse<Void>> softDeleteProduct(
      @PathVariable("product-id") Long productId) {

    productService.softDeleteProduct(productId);
    return ResponseEntity.ok(BaseResponse.success("상품 soft 삭제(HIDDEN 처리)가 완료되었습니다.", null));
  }

  @Override
  public ResponseEntity<BaseResponse<Void>> hardDeleteProduct(
      @PathVariable("product-id") Long productId) {

    productService.hardDeleteProduct(productId);
    return ResponseEntity.ok(BaseResponse.success("상품 hard 삭제가 완료되었습니다.", null));
  }

  @Override
  public ResponseEntity<BaseResponse<List<ProductSummaryResponse>>> getProductsByCategory(
      @PathVariable("category-id") Long categoryId,
      @RequestParam(value = "kiosk-id", required = false) Long kioskId) {

    List<ProductSummaryResponse> responses =
        productService.getProductsByCategory(categoryId, kioskId);

    String message = kioskId == null ? "카테고리별 상품 목록을 조회했습니다." : "키오스크 기준 카테고리별 상품 목록을 조회했습니다.";

    return ResponseEntity.status(HttpStatus.OK).body(BaseResponse.success(message, responses));
  }

  @Override
  public ResponseEntity<BaseResponse<ProductDetailResponse>> getProductDetail(
      @PathVariable("product-id") Long productId) {

    ProductDetailResponse response = productService.getProductDetail(productId);
    return ResponseEntity.status(HttpStatus.OK)
        .body(BaseResponse.success("상품 상세 정보를 조회했습니다.", response));
  }
}
