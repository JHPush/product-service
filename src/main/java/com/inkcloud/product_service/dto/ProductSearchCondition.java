package com.inkcloud.product_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class ProductSearchCondition {

    private String keyword;

    private Long categoryId;

    private String sortBy; // 정렬 조건

    private String sortDir; // 정렬 순서

}
