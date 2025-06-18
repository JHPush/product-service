package com.inkcloud.product_service.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inkcloud.product_service.domain.Category;
import com.inkcloud.product_service.domain.Product;
import com.inkcloud.product_service.domain.Status;
import com.inkcloud.product_service.dto.CategoryCountDto;
import com.inkcloud.product_service.dto.ProductQuantityChangeResult;
import com.inkcloud.product_service.dto.ProductQuantityDeltaDto;
import com.inkcloud.product_service.dto.ProductQuantityResponseDto;
import com.inkcloud.product_service.dto.ProductQuantityUpdateDto;
import com.inkcloud.product_service.dto.ProductRequestDto;
import com.inkcloud.product_service.dto.ProductResponseDto;
import com.inkcloud.product_service.dto.ProductSearchCondition;
import com.inkcloud.product_service.dto.ProductSearchResultDto;
import com.inkcloud.product_service.dto.ProductStatusUpdateDto;
import com.inkcloud.product_service.repository.CategoryRepository;
import com.inkcloud.product_service.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final KafkaTemplate<String, ProductQuantityChangeResult> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public ProductResponseDto createProduct(ProductRequestDto dto) {
        
        Product product = dtoToEntity(dto);
        Product saved = productRepository.save(product);
        log.info("상품 등록 완료: ID={}", saved.getId());

        return entityToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDto getProductById(Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));

        return entityToDto(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductSearchResultDto searchProducts(ProductSearchCondition condition, Pageable pageable) {
        
        // 1. 검색된 상품 페이지 조회
        Page<Product> productPage = productRepository.searchProducts(condition, pageable);
        List<ProductResponseDto> productDtos = productPage.map(this::entityToDto).getContent();

        // 2. 카테고리별 상품 개수 조회
        List<CategoryCountDto> categoryCounts = productRepository.countProductsByCategory(condition);

        // 3. 결과 통합 DTO 구성
        return ProductSearchResultDto.builder()
                .products(new PageImpl<>(productDtos, pageable, productPage.getTotalElements()))
                .categoryCounts(categoryCounts)
                .build();
    }

    @Override
    @Transactional
    public ProductResponseDto updateProduct(Long productId, ProductRequestDto dto) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리가 존재하지 않습니다."));

        product.updateFrom(dto, category);

        log.info("상품 정보 수정 완료: ID={}, 수량={}, 상태={}", productId, product.getQuantity(), product.getStatus());

        return entityToDto(product);
    }

    @Override
    @Transactional
    public void updateProductStatus(Long productId, ProductStatusUpdateDto dto) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));

        product.setStatus(dto.getStatus());
        log.info("상품 상태 변경: ID={}, 상태={}", productId, dto.getStatus());
    }

    @Override
    @Transactional
    public void updateProductQuantity(Long productId, ProductQuantityUpdateDto dto) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));

        product.setQuantity(dto.getQuantity());
        log.info("상품 수량 수정: ID={}, 새 수량={}", productId, dto.getQuantity());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductQuantityResponseDto> getProductQuantity(List<Long> productIds) {
        List<Product> products = productRepository.findAllById(productIds);

        if (products.size() != productIds.size()) {
            throw new IllegalArgumentException("일부 상품이 존재하지 않습니다.");
        }

        return products.stream()
                .map(p -> new ProductQuantityResponseDto(p.getId(), p.getQuantity(), p.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateProductQuantityDelta(ProductQuantityDeltaDto dto) {

        for (ProductQuantityDeltaDto.ProductQuantityDeltaItem item : dto.getDtos()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다. ID=" + item.getProductId()));

            int updatedQuantity = product.getQuantity() - item.getDeltaQuantity();

            if (updatedQuantity < 0) {
                throw new IllegalArgumentException("상품 ID=" + item.getProductId() + "의 재고가 부족합니다.");
            }

            product.setQuantity(updatedQuantity);

            // 상태 변경
            if (updatedQuantity <= 0) {
                product.setStatus(Status.OUT_OF_STOCK);
            } else {
                product.setStatus(Status.ON_SALE);
            }

            productRepository.save(product);
            log.info("상품 ID={} 수량 {} → 최종 수량: {}, 상태: {}", item.getProductId(), item.getDeltaQuantity(), updatedQuantity, product.getStatus());
        }
    }

    @KafkaListener(topics = "stock-change", groupId = "order_group")
    @Transactional
    public void handleStockChangeEvent(String message) {
        
        log.info("Kafka 수신 - 재고 변경 요청: {}", message);
        
        try {
            ProductQuantityDeltaDto dto = objectMapper.readValue(message, ProductQuantityDeltaDto.class);
            boolean success = true;
            try {
                updateProductQuantityDelta(dto);
            } catch (Exception e) {
                log.error("재고 증감 처리 실패: {}", e.getMessage());
                success = false;
            }

            ProductQuantityChangeResult result = new ProductQuantityChangeResult(dto.getOrderId(), success);
            if(dto.getCheck()<0)
                kafkaTemplate.send("stock-confirm", result);
            log.info("재고 처리 결과 발행 완료: {}", result);

        } catch (Exception e) {
            log.error("Kafka 이벤트 처리 중 오류", e);
        }
    }


    // ProductRequestDto → Product
    private Product dtoToEntity(ProductRequestDto dto) {

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리가 존재하지 않습니다."));

        return Product.builder()
                .isbn(dto.getIsbn())
                .name(dto.getName())
                .author(dto.getAuthor())
                .publisher(dto.getPublisher())
                .category(category)
                .price(dto.getPrice())
                .publicationDate(dto.getPublicationDate())
                .introduction(dto.getIntroduction())
                .image(dto.getImage())
                .quantity(dto.getQuantity())
                .rating(0.0)
                .reviewsCount(0)
                .ordersCount(0)
                .status(Status.ON_SALE)
                .build();
    }


    // Product → ProductResponseDto
    private ProductResponseDto entityToDto(Product product) {

        return ProductResponseDto.builder()
                .id(product.getId())
                .isbn(product.getIsbn())
                .name(product.getName())
                .author(product.getAuthor())
                .publisher(product.getPublisher())
                .categoryId(product.getCategory().getId())
                .price(product.getPrice())
                .publicationDate(product.getPublicationDate())
                .introduction(product.getIntroduction())
                .image(product.getImage())
                .quantity(product.getQuantity())
                .rating(product.getRating())
                .reviewsCount(product.getReviewsCount())
                .ordersCount(product.getOrdersCount())
                .status(product.getStatus())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

}
