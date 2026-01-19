package com.laundry.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${external.services.product.url}")
    private String productServiceUrl;

    @Value("${external.services.user.url}")
    private String userServiceUrl;

    @Bean(name = "productWebClient")
    public WebClient productWebClient() {
        return WebClient.builder()
                .baseUrl(productServiceUrl)
                .build();
    }

    @Bean(name = "userWebClient")
    public WebClient userWebClient() {
        return WebClient.builder()
                .baseUrl(userServiceUrl)
                .build();
    }
}
