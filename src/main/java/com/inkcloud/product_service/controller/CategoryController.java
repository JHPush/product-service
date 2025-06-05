package com.inkcloud.product_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inkcloud.product_service.dto.CategoryRequestDto;
import com.inkcloud.product_service.dto.CategoryResponseDto;
import com.inkcloud.product_service.dto.CategoryUpdateDto;
import com.inkcloud.product_service.service.CategoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

    // 1. 카테고리 등록
    @PostMapping
    public ResponseEntity<String> createCategory(@RequestBody CategoryRequestDto requestDto) {

        Long categoryId = categoryService.createCategory(requestDto);
        log.info("카테고리 등록: ID={}, 이름={}", categoryId, requestDto.getName());

        return ResponseEntity.ok("카테고리 등록 완료 (ID: " + categoryId + ")");
    }

    // 2. 카테고리 전체 조회
    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories() {

        List<CategoryResponseDto> categories = categoryService.getAllCategories();
        log.info("전체 카테고리 조회: {}개", categories.size());

        return ResponseEntity.ok(categories);
    }

    // 3. 카테고리 수정
    @PutMapping("/{id}")
    public ResponseEntity<String> updateCategory(@PathVariable Long id,
                                                 @RequestBody CategoryUpdateDto updateDto) {

        categoryService.updateCategory(id, updateDto);
        log.info("카테고리 수정: ID={}, 새 이름={}", id, updateDto.getName());

        return ResponseEntity.ok("카테고리 수정 완료 (ID: " + id + ")");
    }

    // 4. 카테고리 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {

        categoryService.deleteCategory(id);
        log.info("카테고리 삭제: ID={}", id);

        return ResponseEntity.ok("카테고리 삭제 완료 (ID: " + id + ")");
    }
    
}