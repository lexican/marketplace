package com.marketplace.product.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/product")
public class ProductController {

	@GetMapping("/hello")
	public String sayHello() {
		return "Welcome to Product Service";
	}
	
	@GetMapping("/protected")
	public String sayHello2() {
		return "Protected Product Service";
	}

}
