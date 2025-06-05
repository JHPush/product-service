package com.inkcloud.product_service.service;

import java.util.List;

import com.inkcloud.product_service.dto.CategoryRequestDto;
import com.inkcloud.product_service.dto.CategoryResponseDto;
import com.inkcloud.product_service.dto.CategoryUpdateDto;

public interface CategoryService {
    
    Long createCategory(CategoryRequestDto requestDto);

    List<CategoryResponseDto> getAllCategories();

    void updateCategory(Long categoryId, CategoryUpdateDto updateDto);

    void deleteCategory(Long categoryId);
}
