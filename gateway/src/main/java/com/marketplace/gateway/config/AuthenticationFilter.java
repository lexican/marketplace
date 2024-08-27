package com.marketplace.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.gateway.payload.response.VerifyTokenResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RefreshScope
@Component
@RequiredArgsConstructor
public class AuthenticationFilter implements GatewayFilter {

	private final RouterValidator routerValidator;
	private final RestTemplate restTemplate;
	private ObjectMapper objectMapper = new ObjectMapper();;

	@Value("${app.auth.url}")
	private String authUrl;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();

		if (routerValidator.isSecured.test(request)) {
//			System.out.println(
//					"Filter ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: Authorization not available");
			if (this.isAuthMissing(request)) {
				return this.onError(exchange, HttpStatus.UNAUTHORIZED);
			}
			final String authHeader = this.getAuthHeader(request);

			if (authHeader == null || !authHeader.startsWith("Bearer ")) {
				return this.onError(exchange, HttpStatus.UNAUTHORIZED);
			}

			String token = authHeader.substring(7);

			ResponseEntity<String> response = restTemplate.getForEntity(authUrl + token, String.class);

			String body = response.getBody();

			VerifyTokenResponse verifyTokenResponse = VerifyTokenResponse.builder().build();
			try {
				verifyTokenResponse = objectMapper.readValue(body, new TypeReference<VerifyTokenResponse>() {
				});
				exchange.getRequest().mutate().header("email", verifyTokenResponse.getData().getEmail())
						.header("roles", String.join(",", verifyTokenResponse.getData().getRoles())).build();
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}

		}

		return chain.filter(exchange);
	}

	private String getAuthHeader(ServerHttpRequest request) {
		return request.getHeaders().getOrEmpty("Authorization").get(0);
	}

	private Mono<Void> onError(ServerWebExchange exchange, HttpStatus httpStatus) {
		org.springframework.http.server.reactive.ServerHttpResponse response = exchange.getResponse();
		response.setStatusCode(httpStatus);
		return response.setComplete();
	}

	private boolean isAuthMissing(ServerHttpRequest request) {
		return !request.getHeaders().containsKey("Authorization");
	}
}