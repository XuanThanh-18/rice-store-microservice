package com.riceshop.orderservice.config;

import com.riceshop.orderservice.client.PaymentServiceClient;
import com.riceshop.orderservice.service.PaymentIntegrationService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentConfig {

    @Bean
    @ConditionalOnProperty(name = "payment.integration.enabled", havingValue = "true", matchIfMissing = false)
    public PaymentIntegrationService paymentIntegrationService(PaymentServiceClient paymentServiceClient) {
        return new PaymentIntegrationService(paymentServiceClient);
    }
}
