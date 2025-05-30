package com.inkcloud.product_service.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.inkcloud.product_service.domain.Status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class ProductResponseDto {

    private Long id;

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
    
    private double rating;
    
    private int reviewsCount;
    
    private int ordersCount;
    
    private Status status;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;

}
