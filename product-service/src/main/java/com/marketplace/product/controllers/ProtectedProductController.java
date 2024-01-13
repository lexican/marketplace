package com.marketplace.product.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/e-comm")
public class ProtectedProductController {
	@GetMapping("/hello")
	public String sayHello() {
		return "Protected Product Service";
	}
}
