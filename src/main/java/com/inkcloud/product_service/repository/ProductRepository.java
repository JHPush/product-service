package com.inkcloud.product_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inkcloud.product_service.domain.Product;

public interface ProductRepository extends JpaRepository<Product, Long>, CustomProductRepository{

    List<Product> findTop12ByOrderByCreatedAtDesc();

    List<Product> findTop12ByOrderByAverageRatingDesc();

}
