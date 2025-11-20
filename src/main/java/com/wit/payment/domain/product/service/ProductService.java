/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.product.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.wit.payment.domain.product.dto.request.CreateProductRequest;
import com.wit.payment.domain.product.dto.request.UpdateProductRequest;
import com.wit.payment.domain.product.dto.response.ProductDetailResponse;
import com.wit.payment.domain.product.dto.response.ProductSummaryResponse;

/** 상품 관련 비즈니스 로직을 처리하는 서비스 인터페이스입니다. */
public interface ProductService {

  /**
   * 상품을 생성합니다.
   *
   * <p>이미지 리스트에서 첫 번째 이미지는 썸네일(대표 이미지)로 사용되고, 나머지 이미지는 디테일 이미지로 저장합니다.
   *
   * @param storeId 가게 식별자
   * @param request 상품 생성 요청 정보
   * @param images 상품 이미지 리스트 (첫 번째 = 썸네일, 이후 = 디테일 이미지)
   * @return 생성된 상품 상세 정보 응답
   */
  ProductDetailResponse createProduct(
      Long storeId, CreateProductRequest request, List<MultipartFile> images);

  /**
   * 상품 정보를 수정합니다.
   *
   * <p>기존에 등록된 모든 이미지를 삭제한 뒤, 전달받은 이미지 리스트로 전체를 재설정합니다. 첫 번째 이미지는 썸네일, 나머지는 디테일 이미지로 저장됩니다.
   *
   * @param productId 상품 식별자
   * @param request 상품 수정 요청 정보
   * @param images 상품 이미지 리스트 (전체 재설정 기준, 첫 번째 = 썸네일)
   * @return 수정된 상품 상세 정보 응답
   */
  ProductDetailResponse updateProduct(
      Long productId, UpdateProductRequest request, List<MultipartFile> images);

  /**
   * 상품을 소프트 삭제합니다.
   *
   * <p>상품 상태를 {@code HIDDEN}으로 변경합니다.
   *
   * @param productId 상품 식별자
   */
  void softDeleteProduct(Long productId);

  /**
   * 상품을 하드 삭제합니다.
   *
   * <p>DB에서 상품 데이터를 영구 삭제합니다.
   *
   * @param productId 상품 식별자
   */
  void hardDeleteProduct(Long productId);

  /**
   * 특정 가게의 상품 목록을 조회합니다.
   *
   * <p>대표 이미지(썸네일) 한 장만 포함하며, description은 포함하지 않습니다.
   *
   * @param storeId 가게 식별자
   * @return 상품 요약 응답 리스트
   */
  List<ProductSummaryResponse> getProductsByStore(Long storeId);

  /**
   * 상품 상세 정보를 조회합니다.
   *
   * <p>썸네일 및 모든 디테일 이미지를 포함합니다.
   *
   * @param productId 상품 식별자
   * @return 상품 상세 응답
   */
  ProductDetailResponse getProductDetail(Long productId);
}
