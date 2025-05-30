package com.inkcloud.product_service.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class ProductRequestDto {

    private String isbn;

    private String name;

    private String author;

    private String publisher;

    private Long categoryId;

    private int price;

    private LocalDate publicationDate;

    private String introduction;

    private String image;
    
    private int quantity;

}
