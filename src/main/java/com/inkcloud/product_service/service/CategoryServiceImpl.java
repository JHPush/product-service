package com.inkcloud.product_service.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inkcloud.product_service.domain.Category;
import com.inkcloud.product_service.dto.CategoryRequestDto;
import com.inkcloud.product_service.dto.CategoryResponseDto;
import com.inkcloud.product_service.dto.CategoryUpdateDto;
import com.inkcloud.product_service.repository.CategoryRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public Long createCategory(CategoryRequestDto requestDto) {

        Category parent = null;

        if (requestDto.getParentId() != null) {
            parent = categoryRepository.findById(requestDto.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException("상위 카테고리가 존재하지 않습니다."));
        }

        Category category = Category.builder()
                .name(requestDto.getName())
                .parent(parent)
                .build();

        return categoryRepository.save(category).getId();
    }

    @Override
    public List<CategoryResponseDto> getAllCategories() {

        List<Category> categories = categoryRepository.findAll();
        
        return categories.stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void updateCategory(Long categoryId, CategoryUpdateDto updateDto) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("해당 카테고리를 찾을 수 없습니다."));

        category.setName(updateDto.getName());
        category.setUpdatedAt(updateDto.getUpdatedAt());
    }

    @Override
    public void deleteCategory(Long categoryId) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("해당 카테고리를 찾을 수 없습니다."));

        categoryRepository.delete(category);
    }


    private CategoryResponseDto entityToDto(Category category) {

        return new CategoryResponseDto(
                category.getId(),
                category.getName(),
                category.getParent() != null ? category.getParent().getId() : null,
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }

}
