/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.domain.category.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.wit.payment.domain.category.dto.request.CreateCategoryRequest;
import com.wit.payment.domain.category.dto.response.CategoryResponse;
import com.wit.payment.domain.category.entity.Category;

@Component
public class CategoryMapper {

  public Category toCategory(CreateCategoryRequest request) {
    if (request == null) {
      return null;
    }

    return Category.builder().name(request.getName()).build();
  }

  public CategoryResponse toCategoryResponse(Category category) {
    if (category == null) {
      return null;
    }

    return CategoryResponse.builder().id(category.getId()).name(category.getName()).build();
  }

  public List<CategoryResponse> toCategoryResponseList(List<Category> categories) {
    if (categories == null) {
      return List.of();
    }

    return categories.stream().map(this::toCategoryResponse).toList();
  }
}
