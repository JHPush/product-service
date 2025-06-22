package com.inkcloud.product_service.dto;

import com.inkcloud.product_service.domain.Product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductSimpleDto {

    private Long id;

    private String name;

    private String author;

    private String imageUrl;

    private int price;

    private double rating;

    public static ProductSimpleDto from(Product product) {
        return ProductSimpleDto.builder()
                .id(product.getId())
                .name(product.getName())
                .author(product.getAuthor())
                .imageUrl(product.getImage())
                .price(product.getPrice())
                .rating(product.getRating())
                .build();
    }
}
