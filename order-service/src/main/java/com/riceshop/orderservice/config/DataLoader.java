// src/main/java/com/riceshop/orderservice/config/DataLoader.java
package com.riceshop.orderservice.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("dev") // Chỉ chạy trong development environment
public class DataLoader implements CommandLineRunner {

    @Override
    public void run(String... args) {
        log.info("Loading sample data for order service...");

        // Bạn có thể thêm dữ liệu mẫu cho OrderService ở đây

        log.info("Sample data loaded successfully");
    }
}