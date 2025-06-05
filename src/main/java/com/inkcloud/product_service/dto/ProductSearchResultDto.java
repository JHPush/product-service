package com.inkcloud.product_service.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class ProductSearchResultDto {

    private Page<ProductResponseDto> products;

    private List<CategoryCountDto> categoryCounts;
    
}
