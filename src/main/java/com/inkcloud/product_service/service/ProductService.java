package com.inkcloud.product_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.inkcloud.product_service.dto.ProductQuantityUpdateDto;
import com.inkcloud.product_service.dto.ProductRequestDto;
import com.inkcloud.product_service.dto.ProductResponseDto;
import com.inkcloud.product_service.dto.ProductSearchCondition;
import com.inkcloud.product_service.dto.ProductStatusUpdateDto;

public interface ProductService {

    // 상품 등록
    ProductResponseDto createProduct(ProductRequestDto dto);

    // 상품 상세 조회
    ProductResponseDto getProductById(Long productId);

    // 상품 목록 조회 (검색, 페이징, 필터링)
    Page<ProductResponseDto> searchProducts(ProductSearchCondition condition, Pageable pageable);

    // 상품 정보 수정
    ProductResponseDto updateProduct(Long productId, ProductRequestDto dto);

    // 상품 상태 변경
    void updateProductStatus(ProductStatusUpdateDto dto);

    // 재고 수량 변경
    void updateProductQuantity(ProductQuantityUpdateDto dto);

    // 재고 수량 확인
    int getProductQuantity(Long productId);

}
