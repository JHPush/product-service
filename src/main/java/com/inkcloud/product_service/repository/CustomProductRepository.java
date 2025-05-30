package com.inkcloud.product_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.inkcloud.product_service.domain.Product;

public interface CustomProductRepository {

    Page<Product> searchProducts(String keyword, Long categoryId, Pageable pageable);

}
