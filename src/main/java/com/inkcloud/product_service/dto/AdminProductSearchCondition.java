package com.inkcloud.product_service.dto;

import java.util.List;

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
public class AdminProductSearchCondition {

    private String keyword;                   // 검색어

    private List<String> searchFields;        // 검색 대상 필드 (예: 도서명, 저자, 출판사, ISBN)

    private List<Status> statuses;            // 판매 상태 필터 (ON_SALE, OUT_OF_STOCK, DISCONTINUED)

    private List<Long> categoryIds;           // 카테고리 필터

    private String startDate;                 // 출간일 시작 (yyyy-MM-dd)

    private String endDate;                   // 출간일 끝

    private String operator;                  // AND, OR, NOT

    private String sortType;                  // 정렬 조건 (POPULAR, NEWEST, PRICE_ASC, PRICE_DESC)
}
