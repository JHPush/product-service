package com.inkcloud.product_service.init;

import com.inkcloud.product_service.domain.Category;
import com.inkcloud.product_service.domain.Product;
import com.inkcloud.product_service.domain.Status;
import com.inkcloud.product_service.repository.CategoryRepository;
import com.inkcloud.product_service.repository.ProductRepository;
import com.inkcloud.product_service.util.S3UploadUtil;
import com.opencsv.CSVReader;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStreamReader;
import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductDataInitializer {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final S3UploadUtil s3UploadUtil;

    @PostConstruct
    public void initProducts() {
        log.info("상품 초기화 시작");

        try (CSVReader reader = new CSVReader(
                new InputStreamReader(new ClassPathResource("data/products.csv").getInputStream()))) {

            String[] tokens;
            reader.readNext(); // 헤더 건너뜀

            while ((tokens = reader.readNext()) != null) {
                if (tokens.length < 13) continue;

                String name = tokens[0].trim();
                String author = tokens[1].trim();
                String publisher = tokens[2].trim();
                LocalDate pubDate = LocalDate.parse(tokens[3].trim());
                int price = Integer.parseInt(tokens[4].trim());
                String imageUrl = tokens[5].trim();
                double rating = Double.parseDouble(tokens[6].trim());
                int reviewCount = Integer.parseInt(tokens[7].trim());
                Long categoryId = Long.parseLong(tokens[8].trim());
                int quantity = Integer.parseInt(tokens[9].trim());

                String statusStr = tokens[10].trim().toUpperCase();
                Status status;
                try {
                    status = Status.valueOf(statusStr);
                } catch (IllegalArgumentException e) {
                    log.warn("상태값 '{}' 이 잘못되어 기본값 ON_SALE로 대체", statusStr);
                    status = Status.ON_SALE;
                }

                String isbnRaw = tokens[11].trim();
                String isbn = isbnRaw.endsWith(".0") ? isbnRaw.replace(".0", "") : isbnRaw;

                String intro = tokens[12].trim();

                String s3ImageUrl = s3UploadUtil.uploadImageFromUrl(imageUrl, "products");
                if (s3ImageUrl == null) continue;

                Category category = categoryRepository.findById(categoryId).orElse(null);
                if (category == null) {
                    log.warn("존재하지 않는 카테고리 ID: {} → 상품 '{}' 건너뜀", categoryId, name);
                    continue;
                }

                Product product = Product.builder()
                        .name(name)
                        .author(author)
                        .publisher(publisher)
                        .publicationDate(pubDate)
                        .price(price)
                        .image(s3ImageUrl)
                        .rating(rating)
                        .reviewsCount(reviewCount)
                        .category(category)
                        .quantity(quantity)
                        .status(status)
                        .isbn(isbn)
                        .introduction(intro)
                        .build();

                productRepository.save(product);
            }

        } catch (Exception e) {
            log.error("상품 초기화 실패", e);
        }

        log.info("상품 초기화 완료");
    }
}
