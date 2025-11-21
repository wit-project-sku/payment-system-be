/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.category.service;

import java.util.List;

import com.wit.payment.domain.category.dto.request.CreateCategoryRequest;
import com.wit.payment.domain.category.dto.response.CategoryResponse;
import com.wit.payment.domain.category.dto.response.FirstCategoryResponse;

/** 카테고리 관련 비즈니스 로직을 처리하는 서비스 인터페이스입니다. */
public interface CategoryService {

  /**
   * 카테고리를 생성합니다.
   *
   * @param request 카테고리 생성 요청 정보
   * @return 생성된 카테고리 정보 응답
   */
  CategoryResponse createCategory(CreateCategoryRequest request);

  /**
   * 모든 카테고리 목록을 조회합니다.
   *
   * @return 카테고리 정보 응답 리스트
   */
  List<CategoryResponse> getCategories();

  /**
   * 카테고리와 해당 카테고리의 상품들을 하드 삭제합니다.
   *
   * @param categoryId 삭제할 카테고리 식별자
   */
  void deleteCategory(Long categoryId);

  /**
   * 전체 카테고리 목록과, 첫 번째 카테고리의 상품 목록을 함께 반환합니다.
   *
   * <p>프론트 화면에서 첫 로딩 시 상단 탭(카테고리 목록)과 첫 번째 카테고리의 상품 목록을 동시에 렌더링할 수 있도록 설계된 API입니다.
   *
   * @return FirstCategoryResponse 전체 카테고리 목록 + 첫 번째 카테고리의 상품 목록
   */
  FirstCategoryResponse getFirstCategoryWithProducts();
}
