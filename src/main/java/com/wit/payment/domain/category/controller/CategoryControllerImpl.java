/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.category.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.wit.payment.domain.category.dto.request.CreateCategoryRequest;
import com.wit.payment.domain.category.dto.response.CategoryResponse;
import com.wit.payment.domain.category.dto.response.FirstCategoryResponse;
import com.wit.payment.domain.category.service.CategoryService;
import com.wit.payment.global.response.BaseResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CategoryControllerImpl implements CategoryController {

  private final CategoryService categoryService;

  @Override
  public ResponseEntity<BaseResponse<CategoryResponse>> createCategory(
      @RequestBody CreateCategoryRequest request) {

    CategoryResponse response = categoryService.createCategory(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(BaseResponse.success("카테고리 생성이 완료되었습니다.", response));
  }

  @Override
  public ResponseEntity<BaseResponse<List<CategoryResponse>>> getCategories() {
    List<CategoryResponse> responses = categoryService.getCategories();
    return ResponseEntity.status(HttpStatus.OK)
        .body(BaseResponse.success("카테고리 목록을 조회했습니다.", responses));
  }

  @Override
  public ResponseEntity<BaseResponse<Void>> deleteCategory(
      @PathVariable("category-id") Long categoryId) {
    categoryService.deleteCategory(categoryId);
    return ResponseEntity.status(HttpStatus.OK)
        .body(BaseResponse.success("카테고리 삭제가 완료되었습니다.", null));
  }

  @Override
  public ResponseEntity<BaseResponse<FirstCategoryResponse>> getFirstCategoryWithProducts() {
    FirstCategoryResponse response = categoryService.getFirstCategoryWithProducts();
    return ResponseEntity.status(HttpStatus.OK)
        .body(BaseResponse.success("첫 번째 카테고리 및 상품 목록을 조회했습니다.", response));
  }
}
