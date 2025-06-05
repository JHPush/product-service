package com.inkcloud.product_service.repository;

import com.inkcloud.product_service.domain.Product;
import com.inkcloud.product_service.domain.QProduct;
import com.inkcloud.product_service.dto.CategoryCountDto;
import com.inkcloud.product_service.dto.ProductSearchCondition;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
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
    public Page<Product> searchProducts(ProductSearchCondition condition, Pageable pageable) {
        QProduct product = QProduct.product;
        BooleanBuilder builder = new BooleanBuilder();

        // 1. 키워드 + 선택 필드 OR 검색
        if (condition.getKeyword() != null && !condition.getKeyword().isBlank()) {
            BooleanBuilder keywordBuilder = new BooleanBuilder();
            List<String> fields = condition.getSearchFields();

            if (fields != null && !fields.isEmpty()) {
                if (fields.contains("name")) {
                    keywordBuilder.or(product.name.containsIgnoreCase(condition.getKeyword()));
                }
                if (fields.contains("author")) {
                    keywordBuilder.or(product.author.containsIgnoreCase(condition.getKeyword()));
                }
                if (fields.contains("publisher")) {
                    keywordBuilder.or(product.publisher.containsIgnoreCase(condition.getKeyword()));
                }
                if (fields.contains("isbn")) {
                    keywordBuilder.or(product.isbn.containsIgnoreCase(condition.getKeyword()));
                }
                builder.and(keywordBuilder);
            }
        }

        // 2. 카테고리 필터링 (다중 선택)
        if (condition.getCategoryIds() != null && !condition.getCategoryIds().isEmpty()) {
            builder.and(product.category.id.in(condition.getCategoryIds()));
        }

        // 3. 정렬 기준 설정
        OrderSpecifier<?> orderSpecifier = getSortOrder(condition.getSortType());

        List<Product> content = queryFactory
                .selectFrom(product)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderSpecifier)
                .fetch();

        long total = queryFactory
                .select(product.count())
                .from(product)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    private OrderSpecifier<?> getSortOrder(String sortType) {
        QProduct product = QProduct.product;
        if (sortType == null) sortType = "LATEST";

        return switch (sortType.toUpperCase()) {
            case "POPULAR"    -> product.ordersCount.desc();
            case "RATING"     -> product.rating.desc();
            case "PRICE_HIGH" -> product.price.desc();
            case "PRICE_LOW"  -> product.price.asc();
            case "LATEST"     -> product.createdAt.desc();
            default           -> product.createdAt.desc(); // fallback
        };
    }


    @Override
    public List<CategoryCountDto> countProductsByCategory(ProductSearchCondition condition) {
        QProduct product = QProduct.product;
        BooleanBuilder builder = new BooleanBuilder();

        // 검색 조건과 동일하게 구성
        if (condition.getKeyword() != null && !condition.getKeyword().isBlank()) {
            BooleanBuilder keywordBuilder = new BooleanBuilder();
            List<String> fields = condition.getSearchFields();

            if (fields.contains("name")) {
                keywordBuilder.or(product.name.containsIgnoreCase(condition.getKeyword()));
            }
            if (fields.contains("author")) {
                keywordBuilder.or(product.author.containsIgnoreCase(condition.getKeyword()));
            }
            if (fields.contains("publisher")) {
                keywordBuilder.or(product.publisher.containsIgnoreCase(condition.getKeyword()));
            }
            if (fields.contains("isbn")) {
                keywordBuilder.or(product.isbn.containsIgnoreCase(condition.getKeyword()));
            }

            builder.and(keywordBuilder);
        }

        if (condition.getCategoryIds() != null && !condition.getCategoryIds().isEmpty()) {
            builder.and(product.category.id.in(condition.getCategoryIds()));
        }

        // group by 카테고리로 개수 세기
        return queryFactory
                .select(
                    com.querydsl.core.types.Projections.constructor(
                        CategoryCountDto.class,
                        product.category.id,
                        product.category.name,
                        product.count()
                    )
                )
                .from(product)
                .where(builder)
                .groupBy(product.category.id, product.category.name)
                .fetch();
    }
}