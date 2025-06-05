package com.inkcloud.product_service.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.inkcloud.product_service.domain.Product;
import com.inkcloud.product_service.dto.CategoryCountDto;
import com.inkcloud.product_service.dto.ProductSearchCondition;

public interface CustomProductRepository {

    Page<Product> searchProducts(ProductSearchCondition condition, Pageable pageable);

    List<CategoryCountDto> countProductsByCategory(ProductSearchCondition condition);

}
