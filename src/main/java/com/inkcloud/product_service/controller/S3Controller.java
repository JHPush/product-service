package com.inkcloud.product_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inkcloud.product_service.service.S3Service;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/v1/products/image")
@RequiredArgsConstructor
public class S3Controller {

    private final S3Service s3Service;

    @GetMapping("/upload-url")
    public ResponseEntity<String> getUploadUrl(@RequestParam String filename) {
        
        String url = s3Service.generatePresignedUrl(filename);

        return ResponseEntity.ok(url);
    }
    

}
