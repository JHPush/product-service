package com.inkcloud.product_service.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class ErrorResponse {

    private String code;

    private String message;
    
}