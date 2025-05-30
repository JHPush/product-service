package com.inkcloud.product_service.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inkcloud.product_service.dto.ProductQuantityUpdateDto;
import com.inkcloud.product_service.dto.ProductRequestDto;
import com.inkcloud.product_service.dto.ProductResponseDto;
import com.inkcloud.product_service.dto.ProductSearchCondition;
import com.inkcloud.product_service.dto.ProductStatusUpdateDto;
import com.inkcloud.product_service.service.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping(value = "/api/v1/products")
@Slf4j
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 1. 상품 등록
    @PostMapping
    public ProductResponseDto createProduct(@RequestBody ProductRequestDto dto) {

        return productService.createProduct(dto);
    }

    // 2. 상품 상세 조회
    @GetMapping("/{id}")
    public ProductResponseDto getProductById(@PathVariable Long id) {

        return productService.getProductById(id);
    }

    // 3. 상품 검색 + 필터 + 페이징
    @GetMapping
    public Page<ProductResponseDto> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            Pageable pageable
    ) {
        ProductSearchCondition condition = ProductSearchCondition.builder()
                .keyword(keyword)
                .categoryId(categoryId)
                .build();
        return productService.searchProducts(condition, pageable);
    }

    // 4. 상품 정보 수정
    @PutMapping("/{id}")
    public ProductResponseDto updateProduct(
            @PathVariable Long id,
            @RequestBody ProductRequestDto dto
    ) {
        return productService.updateProduct(id, dto);
    }

    // 5. 상품 상태 변경
    @PatchMapping("/status")
    public void updateProductStatus(@RequestBody ProductStatusUpdateDto dto) {
        productService.updateProductStatus(dto);
    }

    // 6. 재고 수량 변경
    @PatchMapping("/quantity")
    public void updateProductQuantity(@RequestBody ProductQuantityUpdateDto dto) {
        productService.updateProductQuantity(dto);
    }

    // 7. 재고 수량 조회
    @GetMapping("/{id}/quantity")
    public int getProductQuantity(@PathVariable Long id) {
        return productService.getProductQuantity(id);
    }
    
}
