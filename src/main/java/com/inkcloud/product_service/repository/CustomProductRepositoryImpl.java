package com.inkcloud.product_service.repository;

import com.inkcloud.product_service.domain.Product;
import com.inkcloud.product_service.domain.QProduct;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@RequiredArgsConstructor
public class CustomProductRepositoryImpl implements CustomProductRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Product> searchProducts(String keyword, Long categoryId, Pageable pageable) {
        QProduct product = QProduct.product;

        BooleanBuilder builder = new BooleanBuilder();

        if (keyword != null && !keyword.isBlank()) {
            builder.and(product.name.containsIgnoreCase(keyword));
        }

        if (categoryId != null) {
            builder.and(product.category.id.eq(categoryId));
        }

        List<Product> content = queryFactory
                .selectFrom(product)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(product.createdAt.desc())  // 기본 정렬
                .fetch();

        long total = queryFactory
                .select(product.count())
                .from(product)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

}
