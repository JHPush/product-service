package com.inkcloud.product_service.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.inkcloud.product_service.domain.QProduct;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

public class QuerydslSortConverter {

    public static OrderSpecifier<?>[] toOrderSpecifiers(QProduct product, Sort sort) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        for (Sort.Order order : sort) {
            ComparableExpressionBase<?> expression = switch (order.getProperty()) {
                case "createdAt" -> product.createdAt;
                case "price" -> product.price;
                case "rating" -> product.rating;
                case "ordersCount" -> product.ordersCount;
                default -> product.createdAt;
            };
            orders.add(new OrderSpecifier(order.isAscending() ? Order.ASC : Order.DESC, expression));
        }

        return orders.toArray(OrderSpecifier[]::new);
    }
}