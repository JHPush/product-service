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
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class CategoryDataInitializer {

    private final CategoryRepository categoryRepository;

    @PostConstruct
    public void initCategories() {
        log.info("📂 카테고리 초기화 시작");

        Map<Long, Category> savedMap = new HashMap<>();
        List<CategoryRow> rows = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource("data/categories.csv").getInputStream()))) {

            reader.lines().skip(1).forEach(line -> {
                String[] tokens = line.split(",", -1);
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

            // 1단계: 모든 카테고리를 parent 없이 먼저 저장 (id 확보)
            for (int i = 0; i < rows.size(); i++) {
                CategoryRow row = rows.get(i);
                Category category = Category.builder()
                        .name(row.name)
                        .order(row.order)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();
                category = categoryRepository.save(category);
                savedMap.put((long) i + 1, category); // CSV 상의 순서 기준 ID 매핑
            }

            // 2단계: parent_id를 기준으로 부모 연결
            for (int i = 0; i < rows.size(); i++) {
                CategoryRow row = rows.get(i);
                if (row.parentId == null) continue;

                Category category = savedMap.get((long) i + 1);
                Category parent = savedMap.get(row.parentId);

                if (category != null && parent != null) {
                    category.setParent(parent);
                    categoryRepository.save(category);
                } else {
                    log.warn("❗ parent 연결 실패 - 카테고리: {}, parent_id: {}", row.name, row.parentId);
                }
            }

        } catch (Exception e) {
            log.error("🚨 카테고리 초기화 실패", e);
        }

        log.info("✅ 카테고리 초기화 완료");
    }

    record CategoryRow(String name, Long parentId, int order) {}
}
