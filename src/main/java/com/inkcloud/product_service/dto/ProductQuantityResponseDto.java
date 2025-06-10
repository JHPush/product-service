package com.inkcloud.product_service.dto;

import com.inkcloud.product_service.domain.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class ProductQuantityResponseDto {

    private Long productId;

    private int quantity;

    private Status status;

}
