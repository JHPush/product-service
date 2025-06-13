package com.inkcloud.product_service.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProductQuantityDeltaDto {

    private List<ProductQuantityDeltaItem> dtos;

    private String orderId;

    private Integer check;

    @Getter @Setter
    public static class ProductQuantityDeltaItem {

        private Long productId;
        
        private int deltaQuantity;
    
    }

}
