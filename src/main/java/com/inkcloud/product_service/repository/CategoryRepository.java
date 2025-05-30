package com.inkcloud.product_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inkcloud.product_service.domain.Category;

public interface CategoryRepository extends JpaRepository<Category, Long>{

}
