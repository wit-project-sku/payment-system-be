/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.product.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.wit.payment.domain.product.dto.request.CreateProductRequest;
import com.wit.payment.domain.product.dto.request.UpdateProductRequest;
import com.wit.payment.domain.product.dto.response.ProductDetailResponse;
import com.wit.payment.domain.product.dto.response.ProductSummaryResponse;
import com.wit.payment.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RequestMapping("/api")
@Tag(name = "Product", description = "키오스크 상품 관련 API")
public interface ProductController {

  @Operation(summary = "상품 생성 API", description = "특정 가게에 상품을 생성합니다.")
  @PostMapping(path = "/stores/{store-id}/products", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  ResponseEntity<BaseResponse<ProductDetailResponse>> createProduct(
      @Parameter(description = "가게 식별자", example = "1") @PathVariable("store-id") Long storeId,
      @Parameter(description = "상품 등록 내용") @RequestPart("data") CreateProductRequest request,
      @Parameter(description = "상품 이미지 리스트 (첫 번째가 대표 이미지)")
          @RequestPart(value = "images", required = false)
          List<MultipartFile> images);

  @Operation(summary = "상품 수정 API", description = "특정 상품 정보를 수정합니다.")
  @PutMapping(path = "/products/{product-id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  ResponseEntity<BaseResponse<ProductDetailResponse>> updateProduct(
      @Parameter(description = "상품 식별자", example = "10") @PathVariable("product-id") Long productId,
      @Parameter(description = "상품 수정 내용") @RequestPart("data") UpdateProductRequest request,
      @Parameter(description = "상품 이미지 리스트 (첫 번째가 대표 이미지)")
          @RequestPart(value = "images", required = false)
          List<MultipartFile> images);

  @Operation(summary = "상품 삭제 API (soft delete)", description = "상품 상태를 HIDDEN으로 변경합니다.")
  @DeleteMapping("/products/{product-id}")
  ResponseEntity<BaseResponse<Void>> softDeleteProduct(
      @Parameter(description = "상품 식별자", example = "10") @PathVariable("product-id")
          Long productId);

  @Operation(summary = "[관리자용] 상품 삭제 API (hard delete)", description = "상품을 영구 삭제합니다.")
  @DeleteMapping("/products/{product-id}/hard")
  ResponseEntity<BaseResponse<Void>> hardDeleteProduct(
      @Parameter(description = "상품 식별자", example = "10") @PathVariable("product-id")
          Long productId);

  @Operation(
      summary = "가게별 상품 목록 조회 API",
      description =
          "storeId에 따른 상품 목록을 조회합니다. " + "대표 이미지 1장과 subTitle만 포함하며 description은 포함하지 않습니다.")
  @GetMapping("/stores/{store-id}/products")
  ResponseEntity<BaseResponse<List<ProductSummaryResponse>>> getProductsByStore(
      @Parameter(description = "가게 식별자", example = "1") @PathVariable("store-id") Long storeId);

  @Operation(
      summary = "상품 상세 조회 API",
      description = "상품 상세페이지에서 사용할 모든 정보(모든 이미지와 description 포함)를 조회합니다.")
  @GetMapping("/products/{product-id}")
  ResponseEntity<BaseResponse<ProductDetailResponse>> getProductDetail(
      @Parameter(description = "상품 식별자", example = "1") @PathVariable("product-id") Long productId);
}
