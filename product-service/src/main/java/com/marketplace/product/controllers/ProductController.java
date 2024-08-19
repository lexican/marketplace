package com.marketplace.product.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.marketplace.product.exceptions.CustomException;
import com.marketplace.product.models.Category;
import com.marketplace.product.models.Product;
import com.marketplace.product.payload.response.ApiResponse;
import com.marketplace.product.services.CategoryService;
import com.marketplace.product.services.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

	private final ProductService productService;
	private final CategoryService categoryService;
	
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Object>> handleUserNotFoundException(CustomException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.builder().success(true).data(null).error(ex.getMessage()).build());
    }
    // Returns all products
	@GetMapping("/all")
	public ResponseEntity<ApiResponse<Object>> getProducts() {
		List<Product> products = (List<Product>) productService.getProducts();
		return ResponseEntity.ok(ApiResponse.builder().success(true).data(products).build());
	}
	
	@GetMapping("/categories")
	public ResponseEntity<ApiResponse<Object>> getCategories() {
		List<Category> categories = (List<Category>) categoryService.getCategories();
		return ResponseEntity.ok(ApiResponse.builder().success(true).data(categories).build());
	}

	@GetMapping("/welcome")
	public String getWelcomeMessage() {
		return "Welcome to Product Service!";
	}

}
