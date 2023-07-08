//package com.marketplace.gateway;
//
//import org.springframework.cloud.gateway.route.RouteLocator;
//import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class SpringCloudConfig {
//
//	@Bean
//	public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
//		System.out.println("builder : " + builder.toString());
//		return builder.routes().route(r -> r.path("/api/auth/**").uri("http://localhost:8080/")).build();
//	}
//
//}