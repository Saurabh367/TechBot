package com.techSuggestion.bot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    // Or if you want a singleton WebClient:
    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.build();
    }
}
