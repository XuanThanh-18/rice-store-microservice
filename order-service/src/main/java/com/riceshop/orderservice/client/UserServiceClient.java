// src/main/java/com/riceshop/orderservice/client/UserServiceClient.java
package com.riceshop.orderservice.client;

import com.riceshop.orderservice.dto.external.UserDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping("/api/users/{id}")
    @CircuitBreaker(name = "userService", fallbackMethod = "getUserFallback")
    ResponseEntity<UserDto> getUser(@PathVariable Long id);

    default ResponseEntity<UserDto> getUserFallback(Long id, Throwable throwable) {
        return ResponseEntity.ok(UserDto.builder()
                .id(id)
                .username("Fallback User")
                .email("fallback@example.com")
                .build());
    }
}