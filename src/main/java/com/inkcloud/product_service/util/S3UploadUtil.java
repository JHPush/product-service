package com.inkcloud.product_service.util;

import com.inkcloud.product_service.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3UploadUtil {

    private final S3Service s3Service;

    public String uploadImageFromUrl(String imageUrl, String filename) {

        String s3Key = "products/" + filename;
        
        try (InputStream inputStream = new URL(imageUrl).openStream()) {
            String presignedUrl = s3Service.generatePresignedUrl(s3Key);
            HttpURLConnection connection = (HttpURLConnection) new URL(presignedUrl).openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "image/jpeg");

            try (OutputStream out = connection.getOutputStream()) {
                inputStream.transferTo(out);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                log.info("[S3] 업로드 성공: {}", filename);
                return s3Service.getPublicUrl(filename); // S3 공개 URL 반환
            } else {
                log.error("[S3] 업로드 실패: {} - {}", responseCode, filename);
                return null;
            }

        } catch (Exception e) {
            log.error("[S3] 이미지 업로드 예외", e);
            return null;
        }
    }
}
