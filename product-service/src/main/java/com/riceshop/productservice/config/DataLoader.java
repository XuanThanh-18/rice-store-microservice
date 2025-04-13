// src/main/java/com/riceshop/productservice/config/DataLoader.java
package com.riceshop.productservice.config;

import com.riceshop.productservice.entity.Category;
import com.riceshop.productservice.entity.Product;
import com.riceshop.productservice.repository.CategoryRepository;
import com.riceshop.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception {
        loadCategories();
        loadProducts();
    }

    private void loadCategories() {
        if (categoryRepository.count() == 0) {
            try {
                List<Category> categories = Arrays.asList(
                        Category.builder().name("White Rice").description("Various types of white rice").build(),
                        Category.builder().name("Brown Rice").description("Healthier brown rice options").build(),
                        Category.builder().name("Jasmine Rice").description("Aromatic jasmine rice varieties").build(),
                        Category.builder().name("Basmati Rice").description("Long-grain basmati rice").build(),
                        Category.builder().name("Sticky Rice").description("Glutinous rice for desserts and special dishes").build(),
                        Category.builder().name("Organic Rice").description("Certified organic rice products").build()
                );

                categoryRepository.saveAll(categories);
                log.info("Sample Categories loaded");
            } catch (Exception e) {
                log.error("Failed to load categories", e);
            }
        }
    }

    private void loadProducts() {
        if (productRepository.count() == 0) {
            try {
                Category whiteRice = categoryRepository.findByName("White Rice")
                        .orElseThrow(() -> new RuntimeException("Category not found"));

                Category brownRice = categoryRepository.findByName("Brown Rice")
                        .orElseThrow(() -> new RuntimeException("Category not found"));

                Category jasmineRice = categoryRepository.findByName("Jasmine Rice")
                        .orElseThrow(() -> new RuntimeException("Category not found"));

                List<Product> products = Arrays.asList(
                        Product.builder()
                                .name("Premium White Rice")
                                .description("High-quality white rice, perfect for everyday meals")
                                .price(new BigDecimal("12.99"))
                                .stockQuantity(100)
                                .category(whiteRice)
                                .riceType("White Long Grain")
                                .origin("Vietnam")
                                .packaging("Cloth Bag")
                                .weight(5.0)
                                .isOrganic(false)
                                .cookingInstructions("Rinse rice before cooking. Use 1.5 cups of water for 1 cup of rice. Bring to boil, then simmer covered for 15 minutes.")
                                .nutritionFacts("Serving Size: 1/4 cup (45g), Calories: 160, Total Fat: 0g, Sodium: 0mg, Total Carbohydrate: 36g, Protein: 3g")
                                .build(),

                        Product.builder()
                                .name("Organic Brown Rice")
                                .description("100% organic brown rice, rich in fiber and nutrients")
                                .price(new BigDecimal("15.99"))
                                .stockQuantity(75)
                                .category(brownRice)
                                .riceType("Brown Long Grain")
                                .origin("Thailand")
                                .packaging("Paper Bag")
                                .weight(2.0)
                                .isOrganic(true)
                                .cookingInstructions("Rinse rice before cooking. Use 2 cups of water for 1 cup of rice. Bring to boil, then simmer covered for 45 minutes.")
                                .nutritionFacts("Serving Size: 1/4 cup (45g), Calories: 150, Total Fat: 1g, Sodium: 0mg, Total Carbohydrate: 32g, Dietary Fiber: 2g, Protein: 3g")
                                .build(),

                        Product.builder()
                                .name("Premium Jasmine Rice")
                                .description("Fragrant jasmine rice from Thailand")
                                .price(new BigDecimal("18.99"))
                                .stockQuantity(50)
                                .category(jasmineRice)
                                .riceType("Jasmine")
                                .origin("Thailand")
                                .packaging("Plastic Bag")
                                .weight(10.0)
                                .isOrganic(false)
                                .cookingInstructions("Rinse rice before cooking. Use 1.5 cups of water for 1 cup of rice. Bring to boil, then simmer covered for 15-20 minutes.")
                                .nutritionFacts("Serving Size: 1/4 cup (45g), Calories: 160, Total Fat: 0g, Sodium: 0mg, Total Carbohydrate: 36g, Protein: 3g")
                                .build()
                );

                productRepository.saveAll(products);
                log.info("Sample Products loaded");
            } catch (Exception e) {
                log.error("Failed to load products", e);
            }
        }
    }
}