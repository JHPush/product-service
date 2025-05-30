package com.inkcloud.product_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inkcloud.product_service.domain.Category;
import com.inkcloud.product_service.domain.Product;
import com.inkcloud.product_service.domain.Status;
import com.inkcloud.product_service.dto.ProductQuantityUpdateDto;
import com.inkcloud.product_service.dto.ProductRequestDto;
import com.inkcloud.product_service.dto.ProductResponseDto;
import com.inkcloud.product_service.dto.ProductSearchCondition;
import com.inkcloud.product_service.dto.ProductStatusUpdateDto;
import com.inkcloud.product_service.repository.CategoryRepository;
import com.inkcloud.product_service.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public ProductResponseDto createProduct(ProductRequestDto dto) {
        
        Product product = dtoToEntity(dto);
        Product saved = productRepository.save(product);
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
    public Page<ProductResponseDto> searchProducts(ProductSearchCondition condition, Pageable pageable) {

        Page<Product> page = productRepository.searchProducts(condition.getKeyword(), condition.getCategoryId(), pageable);
        return page.map(this::entityToDto);
    }

    @Override
    @Transactional
    public ProductResponseDto updateProduct(Long productId, ProductRequestDto dto) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리가 존재하지 않습니다."));

        product.setName(dto.getName());
        product.setIsbn(dto.getIsbn());
        product.setAuthor(dto.getAuthor());
        product.setPublisher(dto.getPublisher());
        product.setCategory(category);
        product.setPrice(dto.getPrice());
        product.setPublicationDate(dto.getPublicationDate());
        product.setIntroduction(dto.getIntroduction());
        product.setImage(dto.getImage());
        product.setQuantity(dto.getQuantity());

        return entityToDto(product);
    }

    @Override
    @Transactional
    public void updateProductStatus(ProductStatusUpdateDto dto) {

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));
        product.setStatus(dto.getStatus());
    }

    @Override
    @Transactional
    public void updateProductQuantity(ProductQuantityUpdateDto dto) {

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));
        product.setQuantity(dto.getQuantity());
    }

    @Override
    @Transactional(readOnly = true)
    public int getProductQuantity(Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));
        return product.getQuantity();
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
