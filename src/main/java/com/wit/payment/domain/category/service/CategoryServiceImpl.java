/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.category.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wit.payment.domain.category.dto.request.CreateCategoryRequest;
import com.wit.payment.domain.category.dto.response.CategoryResponse;
import com.wit.payment.domain.category.dto.response.FirstCategoryResponse;
import com.wit.payment.domain.category.entity.Category;
import com.wit.payment.domain.category.exception.CategoryErrorCode;
import com.wit.payment.domain.category.mapper.CategoryMapper;
import com.wit.payment.domain.category.repository.CategoryRepository;
import com.wit.payment.domain.product.dto.response.ProductSummaryResponse;
import com.wit.payment.domain.product.repository.ProductRepository;
import com.wit.payment.domain.product.service.ProductService;
import com.wit.payment.global.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

  private final CategoryRepository categoryRepository;
  private final ProductRepository productRepository;
  private final CategoryMapper categoryMapper;
  private final ProductService productService;

  @Override
  @Transactional
  public CategoryResponse createCategory(CreateCategoryRequest request) {
    Category category = categoryMapper.toCategory(request);

    Category saved = categoryRepository.save(category);
    log.info("카테고리 생성 성공 - categoryId: {}", saved.getId());

    return categoryMapper.toCategoryResponse(saved);
  }

  @Override
  public List<CategoryResponse> getCategories() {
    List<Category> categories = categoryRepository.findAll();

    List<CategoryResponse> responses = categoryMapper.toCategoryResponseList(categories);

    log.info("전체 카테고리 조회 성공 - count: {}", responses.size());
    return responses;
  }

  @Override
  @Transactional
  public void deleteCategory(Long categoryId) {
    Category category =
        categoryRepository
            .findById(categoryId)
            .orElseThrow(() -> new CustomException(CategoryErrorCode.CATEGORY_NOT_FOUND));

    long deletedCount = productRepository.deleteByCategoryId(categoryId);
    log.info(
        "카테고리 연관 상품 삭제 성공 - categoryId: {}, deletedProductCount: {}", categoryId, deletedCount);

    categoryRepository.delete(category);
    log.info("카테고리 삭제 성공(hard delete) - categoryId: {}", categoryId);
  }

  @Override
  public FirstCategoryResponse getFirstCategoryWithProducts() {

    List<Category> categories = categoryRepository.findAll();

    if (categories.isEmpty()) {
      throw new CustomException(CategoryErrorCode.CATEGORY_NOT_FOUND);
    }

    // 첫번째로 조회할 스토어 선택
    Category firstCategory = categories.get(0);

    List<ProductSummaryResponse> products =
        productService.getProductsByCategory(firstCategory.getId());

    log.info(
        "첫 번째 카테고리의 상품 조회 완료 - categoryId: {}, count: {}", firstCategory.getId(), products.size());

    return new FirstCategoryResponse(
        categoryMapper.toCategoryResponseList(categories),
        firstCategory.getId(),
        firstCategory.getName(),
        products);
  }
}
