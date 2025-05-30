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
public class CategoryUpdateDto {

    private Long id;
    
    private String name;

    private Long parentId;

}
