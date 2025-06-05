package com.inkcloud.product_service.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inkcloud.product_service.dto.ProductQuantityDeltaDto;
import com.inkcloud.product_service.dto.ProductQuantityUpdateDto;
import com.inkcloud.product_service.dto.ProductRequestDto;
import com.inkcloud.product_service.dto.ProductResponseDto;
import com.inkcloud.product_service.dto.ProductSearchCondition;
import com.inkcloud.product_service.dto.ProductSearchResultDto;
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
    public ResponseEntity<ProductResponseDto> createProduct(@RequestBody ProductRequestDto dto) {

        ProductResponseDto created = productService.createProduct(dto);
        log.info("상품 등록: ID={}, 이름={}", created.getId(), created.getName());

        return ResponseEntity.ok(created);
    }

    // 2. 상품 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long id) {

        ProductResponseDto product = productService.getProductById(id);
        log.info("상품 조회: ID={}", id);

        return ResponseEntity.ok(product);
    }

    // 3. 상품 검색 + 필터 + 페이징
    @GetMapping("/search")
    public ResponseEntity<ProductSearchResultDto> searchProducts(
            @ModelAttribute ProductSearchCondition condition,
            Pageable pageable) {

        ProductSearchResultDto result = productService.searchProducts(condition, pageable);

        log.info("상품 검색 요청: keyword={}, fields={}, categories={}, sort={}, 총 {}건",
                condition.getKeyword(),
                condition.getSearchFields(),
                condition.getCategoryIds(),
                condition.getSortType(),
                result.getProducts().getTotalElements());

        return ResponseEntity.ok(result);
    }

    // 4. 상품 정보 수정
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductRequestDto dto) {

        ProductResponseDto updated = productService.updateProduct(id, dto);
        log.info("상품 정보 수정: ID={}", id);

        return ResponseEntity.ok(updated);
    }

    // 5. 상품 상태 변경
    @PatchMapping("/{id}/status")
    public ResponseEntity<String> updateProductStatus(
            @PathVariable Long id,
            @RequestBody ProductStatusUpdateDto dto) {

        productService.updateProductStatus(id, dto);
        log.info("상품 상태 변경: ID={}, status={}", id, dto.getStatus());

        return ResponseEntity.ok("상품 상태가 변경되었습니다.");
    }

    // 6. 재고 수량 변경
    @PatchMapping("/{id}/quantity")
    public ResponseEntity<String> updateProductQuantity(
            @PathVariable Long id,
            @RequestBody ProductQuantityUpdateDto dto) {

        productService.updateProductQuantity(id, dto);
        log.info("상품 재고 수정: ID={}, 수량={}", id, dto.getQuantity());

        return ResponseEntity.ok("재고 수량이 수정되었습니다.");
    }

    // 7. 재고 수량 조회
    @GetMapping("/{id}/quantity")
    public ResponseEntity<Integer> getProductQuantity(@PathVariable Long id) {

        int quantity = productService.getProductQuantity(id);
        log.info("상품 재고 조회: ID={}, 수량={}", id, quantity);

        return ResponseEntity.ok(quantity);
    }

    // 8. 재고 증감
    @PatchMapping("/{id}/quantity-delta")
    public ResponseEntity<String> updateProductQuantityDelta(
            @PathVariable Long id,
            @RequestBody ProductQuantityDeltaDto dto) {

        productService.updateProductQuantityDelta(id, dto);
        log.info("상품 재고 증감: ID={}, 변화량={}", id, dto.getQuantityDelta());
        
        return ResponseEntity.ok("재고가 변경되었습니다.");
    }

}