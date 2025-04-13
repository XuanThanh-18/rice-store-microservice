// src/main/java/com/riceshop/orderservice/config/OpenApiConfig.java
package com.riceshop.orderservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Order Service API")
                        .description("API for managing orders and shopping carts for Rice Shop")
                        .version("1.0")
                        .contact(new Contact().name("Rice Shop Team").email("contact@riceshop.com"))
                        .license(new License().name("Apache 2.0").url("http://www.apache.org/licenses/LICENSE-2.0")));
    }
}