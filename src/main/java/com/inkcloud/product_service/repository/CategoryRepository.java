package com.inkcloud.product_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.inkcloud.product_service.domain.Category;

public interface CategoryRepository extends JpaRepository<Category, Long>{

    @Query("SELECT MAX(c.order) FROM Category c WHERE " +
       "(:parentId IS NULL AND c.parent IS NULL) OR " +
       "(c.parent.id = :parentId)")
    Integer findMaxOrderByParentId(@Param("parentId") Long parentId);

}
