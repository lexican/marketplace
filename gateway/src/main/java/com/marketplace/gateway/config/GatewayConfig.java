package com.marketplace.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
//@EnableHystrix
@RequiredArgsConstructor
public class GatewayConfig {

    private final AuthenticationFilter filter;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r.path("/api/auth-service/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://auth-service"))

                .route("product-service", r -> r.path("/api/product-service/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://product-service"))
                .build();
    }

}