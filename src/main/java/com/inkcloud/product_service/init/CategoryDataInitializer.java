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
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class CategoryDataInitializer {

    private final CategoryRepository categoryRepository;

    @PostConstruct
    public void initCategories() {
        log.info("📂 카테고리 초기화 시작");

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource("data/categories.csv").getInputStream()))) {

            Map<String, Category> created = new HashMap<>();
            List<CategoryRow> rows = new ArrayList<>();

            // CSV 파싱
            reader.lines().skip(1).forEach(line -> {
                String[] tokens = line.split(",", -1);
                String name = tokens[0].trim();
                String parentId = tokens[1].trim();
                int order = Integer.parseInt(tokens[2].trim());

                rows.add(new CategoryRow(name, parentId.isEmpty() ? null : parentId, order));
            });

            // 1차: 루트 카테고리부터 삽입
            for (CategoryRow row : rows) {
                if (row.parentId == null) {
                    Category cat = Category.builder()
                            .name(row.name)
                            .order(row.order)
                            .build();
                    created.put(row.name, categoryRepository.save(cat));
                }
            }

            // 2차: 하위 카테고리 연결
            for (CategoryRow row : rows) {
                if (row.parentId != null) {
                    Category parent = created.get(row.parentId);
                    if (parent == null) {
                        log.warn("상위 카테고리 '{}' 를 찾을 수 없어 생략", row.parentId);
                        continue;
                    }
                    Category child = Category.builder()
                            .name(row.name)
                            .parent(parent)
                            .order(row.order)
                            .build();
                    created.put(row.name, categoryRepository.save(child));
                }
            }

        } catch (Exception e) {
            log.error("카테고리 초기화 실패", e);
        }

        log.info("카테고리 초기화 완료");
    }

    record CategoryRow(String name, String parentId, int order) {}
}
