package com.inkcloud.product_service.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProductQuantityDeltaDto {

    private List<ProductQuantityDeltaItem> items;

    private String orderId;

    @Getter @Setter
    public static class ProductQuantityDeltaItem {

        private Long productId;
        
        private int quantityDelta;
    
    }

}
