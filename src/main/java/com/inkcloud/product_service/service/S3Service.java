package com.inkcloud.product_service.service;

import java.net.URL;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
// import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
// import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public String generatePresignedUrl(String filename) {
        log.info("[S3Service] Presigned URL 생성 시작 - 파일명: {}", filename);

        DefaultCredentialsProvider credentialsProvider = DefaultCredentialsProvider.create();
        AwsCredentials credentials = credentialsProvider.resolveCredentials();
        log.info("[S3Service] 사용 중인 IAM 역할의 AccessKeyId: {}", credentials.accessKeyId());

        S3Presigner presigner = S3Presigner.builder()
            .region(Region.of(region))
            .credentialsProvider(credentialsProvider)
            .build();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key("products/" + filename)
            //.acl("bucket-owner-full-control")
            .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
            .putObjectRequest(putObjectRequest)
            .signatureDuration(Duration.ofMinutes(5))
            .build();

        URL url = presigner.presignPutObject(presignRequest).url();
        log.info("[S3Service] Presigned URL 생성 완료: {}", url);

        presigner.close();
        return url.toString();
    }
}





    //     @Value("${cloud.aws.credentials.access-key}")
    // private String accessKey;

    // @Value("${cloud.aws.credentials.secret-key}")
    // private String secretKey;

    // @Value("${cloud.aws.region.static}")
    // private String region;

    // @Value("${cloud.aws.s3.bucket}")
    // private String bucketName;

    // public String generatePresignedUrl(String filename) {

    //     S3Presigner presigner = S3Presigner.builder()
    //         .region(Region.of(region))
    //         .credentialsProvider(StaticCredentialsProvider.create(
    //             AwsBasicCredentials.create(accessKey, secretKey)
    //         ))
    //         .build();

    //     PutObjectRequest putObjectRequest = PutObjectRequest.builder()
    //         .bucket(bucketName)
    //         .key("products/" + filename)
    //         .build();

    //     PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
    //         .putObjectRequest(putObjectRequest)
    //         .signatureDuration(Duration.ofMinutes(5))
    //         .build();

    //     URL url = presigner.presignPutObject(presignRequest).url();

    //     presigner.close();
    //     return url.toString();
    // }