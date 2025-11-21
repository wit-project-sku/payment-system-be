/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.category.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.wit.payment.domain.category.dto.request.CreateCategoryRequest;
import com.wit.payment.domain.category.dto.response.CategoryResponse;
import com.wit.payment.domain.category.dto.response.FirstCategoryResponse;
import com.wit.payment.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RequestMapping("/api/categories")
@Tag(name = "Category", description = "키오스크 카테고리 관련 API")
public interface CategoryController {

  @Operation(summary = "카테고리 생성 API", description = "키오스크에서 사용할 카테고리를 생성합니다.")
  @PostMapping
  ResponseEntity<BaseResponse<CategoryResponse>> createCategory(
      @RequestBody CreateCategoryRequest request);

  @Operation(summary = "전체 카테고리 조회 API", description = "모든 카테고리 목록을 조회합니다.")
  @GetMapping
  ResponseEntity<BaseResponse<List<CategoryResponse>>> getCategories();

  @Operation(summary = "카테고리 삭제 API (hard delete)", description = "카테고리를 영구 삭제합니다.")
  @DeleteMapping("/{category-id}")
  ResponseEntity<BaseResponse<Void>> deleteCategory(@PathVariable("category-id") Long categoryId);

  @Operation(
      summary = "초기 화면 데이터 조회 API (카테고리 목록 + 첫 카테고리 상품)",
      description = "첫 화면 렌더링을 위해 전체 카테고리 목록과 첫 번째 카테고리의 상품 목록을 동시에 반환합니다.")
  @GetMapping("/first")
  ResponseEntity<BaseResponse<FirstCategoryResponse>> getFirstCategoryWithProducts();
}
