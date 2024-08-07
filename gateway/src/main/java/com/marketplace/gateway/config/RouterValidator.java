package com.marketplace.gateway.config;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;


@Component
public class RouterValidator {

    public static final List<String> openApiEndpoints = List.of(
            "/api/product/hello", "/api/auth/login", "/api/auth/register", "/api/auth/validate"
    );

    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));

}