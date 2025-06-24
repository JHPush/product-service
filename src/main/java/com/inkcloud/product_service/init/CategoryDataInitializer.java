package com.inkcloud.product_service.init;

import com.inkcloud.product_service.domain.Category;
import com.inkcloud.product_service.repository.CategoryRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class CategoryDataInitializer {

    private final CategoryRepository categoryRepository;

    @PostConstruct
    public void initCategories() {
        log.info("카테고리 초기화 시작");

        Map<Long, Category> savedMap = new HashMap<>();
        List<CategoryRow> rows = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource("data/categories.csv").getInputStream(), StandardCharsets.UTF_8))) {

            // CSV 파싱
            reader.lines().skip(1).forEach(line -> {
                String[] tokens = line.split(",", -1);
                if (tokens.length < 3) return;

                String name = tokens[0].trim();
                String parentIdStr = tokens[1].trim();
                int order = Integer.parseInt(tokens[2].trim());

                Long parentId = null;
                if (!parentIdStr.isBlank()) {
                    try {
                        parentId = Long.parseLong(parentIdStr);
                    } catch (NumberFormatException e) {
                        log.warn("잘못된 parent_id 형식: {}", parentIdStr);
                    }
                }

                rows.add(new CategoryRow(name, parentId, order));
            });

            // 1단계: 상위 카테고리 먼저 저장
            for (int i = 0; i < rows.size(); i++) {
                CategoryRow row = rows.get(i);
                if (row.parentId == null) {
                    Category category = Category.builder()
                            .name(row.name)
                            .order(row.order)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();
                    Category saved = categoryRepository.save(category);
                    savedMap.put((long) i + 1, saved);  // CSV 순서를 기준으로 ID 매핑
                }
            }

            // 2단계: 하위 카테고리 저장 + parent 연결
            for (int i = 0; i < rows.size(); i++) {
                CategoryRow row = rows.get(i);
                if (row.parentId != null) {
                    Category parent = savedMap.get(row.parentId);
                    if (parent == null) {
                        log.warn("parent 연결 실패 - 카테고리: {}, parent_id: {}", row.name, row.parentId);
                        continue;
                    }

                    Category child = Category.builder()
                            .name(row.name)
                            .order(row.order)
                            .parent(parent)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();
                    Category saved = categoryRepository.save(child);
                    savedMap.put((long) i + 1, saved);
                }
            }

        } catch (Exception e) {
            log.error("카테고리 초기화 실패", e);
        }

        log.info("카테고리 초기화 완료");
    }

    record CategoryRow(String name, Long parentId, int order) {}
}
