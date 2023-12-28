package com.marketplace.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.marketplace.gateway.payload.response.VerifyTokenResponse;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

	@Autowired
	private RouteValidator validator;

	@Autowired
	private RestTemplate template;

	public AuthenticationFilter() {
		super(Config.class);
	}

	@Override
	public GatewayFilter apply(Config config) {
		//System.out.println("exchange.getRequest() : " + config.toString());
		return ((exchange, chain) -> {
			//System.out.println("exchange.getRequest() : " + exchange.getRequest().getBody());
			if (validator.isSecured.test(exchange.getRequest())) {
				// header contains token or not
				if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
					throw new RuntimeException("missing authorization header");
				}

				String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
				
				System.out.println("authHeader : " + authHeader.toString());
//				if (authHeader != null && authHeader.startsWith("Bearer ")) {
//					authHeader = authHeader.substring(7);
//				}
//				try {
//					System.out.println("authHeader : " + authHeader.toString());
//					// REST call to AUTH service
//					VerifyTokenResponse response = template
//							.getForObject("http://auth-service/api/auth/validate?token=" + authHeader, VerifyTokenResponse.class);
//
//					String responseJson = response.toString();
//
//					System.out.println(responseJson);
//
//				} catch (Exception e) {
//					System.out.println("invalid access...!" + e.toString());
//					throw new RuntimeException("un authorized access to application");
//				}
			}
			return chain.filter(exchange);
		});
	}

	public static class Config {

	}
}
//
//System.out.println("authHeader : " + authHeader.toString());
//// REST call to AUTH service
//AuthenticationResponse response = template
//		.getForObject("http://localhost:8082/api/auth/validate?token=" + authHeader, AuthenticationResponse.class);
//
//String responseJson = response.toString();
//
//System.out.println(responseJson);