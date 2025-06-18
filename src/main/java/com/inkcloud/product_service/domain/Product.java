package com.inkcloud.product_service.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.inkcloud.product_service.dto.ProductRequestDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String isbn;

    private String name;

    private String author;

    private String publisher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    private int price;

    private LocalDate publicationDate;

    @Column(length = 1000)
    private String introduction;

    private String image; // S3 URL

    private int quantity;

    private double rating; // 평균 평점

    private int reviewsCount; // 리뷰 개수

    private int ordersCount; // 주문량

    @Enumerated(EnumType.STRING)
    private Status status; // 상품 상태 정보

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


    @PrePersist
    protected void onCreate() {
        this.createdAt = this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

<<<<<<< HEAD
    public void updateFrom(ProductRequestDto dto, Category category) {
        this.name = dto.getName();
        this.isbn = dto.getIsbn();
        this.author = dto.getAuthor();
        this.publisher = dto.getPublisher();
        this.category = category;
        this.price = dto.getPrice();
        this.publicationDate = dto.getPublicationDate();
        this.introduction = dto.getIntroduction();
        this.image = dto.getImage();
        this.quantity = dto.getQuantity();

        // 상태 수동 설정 (DISCONTINUED는 수동으로만 설정 가능)
        if (dto.getStatus() != null) {
            this.status = dto.getStatus();
        }

        // 조건 1: 재고가 0이면 무조건 품절
        if (this.quantity <= 0) {
            this.status = Status.OUT_OF_STOCK;
        }

        // 조건 2 & 3: 재고가 0 이상일 때만 판매 상태 자동 복귀
        if (this.quantity > 0 && (this.status != Status.DISCONTINUED)) {
            this.status = Status.ON_SALE;
        }
    }
=======
    // 소수 첫째자리로 반올림
    // public void setRating(double rating) {
    //     this.rating = Math.round(rating * 10) / 10.0;
    // }

    // public double getRating() {
    //     return rating;
    // }
>>>>>>> feature-100

}
