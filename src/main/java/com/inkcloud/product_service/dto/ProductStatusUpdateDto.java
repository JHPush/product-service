package com.inkcloud.product_service.dto;

import com.inkcloud.product_service.domain.Status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class ProductStatusUpdateDto {

    private Long productId;

    private Status status;
    
}
