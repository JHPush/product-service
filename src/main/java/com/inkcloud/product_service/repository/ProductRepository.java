package com.inkcloud.product_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inkcloud.product_service.domain.Product;

public interface ProductRepository extends JpaRepository<Product, Long>, CustomProductRepository{

}
