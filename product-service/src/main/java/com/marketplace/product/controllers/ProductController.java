package com.marketplace.product.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.marketplace.product.models.Category;
import com.marketplace.product.models.Product;
import com.marketplace.product.payload.request.CreateProductRequest;
import com.marketplace.product.payload.response.ApiResponse;
import com.marketplace.product.services.CategoryService;
import com.marketplace.product.services.ProductService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/product-service")
@RequiredArgsConstructor
public class ProductController {

	private final ProductService productService;
	private final CategoryService categoryService;

	// Returns all products
	@GetMapping("/products")
	public ResponseEntity<ApiResponse<Object>> getProducts() {
		List<Product> products = (List<Product>) productService.getProducts();
		return ResponseEntity.ok(
				ApiResponse.builder().success(true).data(products).message("Products fetched successfully").build());
	}

	// Creates a new product
	@PostMapping("/products")
	public ResponseEntity<ApiResponse<Object>> createProduct(
			@Valid @RequestBody CreateProductRequest createProductRequest) {

		Optional<Category> category = categoryService
				.getCategoryById(Long.valueOf(createProductRequest.getCategoryId()));

		if (category.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(ApiResponse.builder().success(false).data(null).message("Category does not exist").build());
		}

		Product product = Product.builder().name(createProductRequest.getName())
				.description(createProductRequest.getDescription()).quantity(createProductRequest.getQuantity())
				.price(createProductRequest.getPrice()).productDiscount(createProductRequest.getDiscount())
				.sku(createProductRequest.getSku()).category(category.get()).build();
		
		Product newProduct = productService.createProduct(product);

		return ResponseEntity
				.ok(ApiResponse.builder().success(true).data(newProduct).message("Product created successfully").build());
	}

	// Returns a single product by Id
	@GetMapping("/products/{id}")
	public ResponseEntity<ApiResponse<Object>> getProductById(@PathVariable("id") @Positive Long productId) {
		Optional<Product> product = productService.getProductById(productId);
		if (product.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(ApiResponse.builder().success(false).data(null).message("Product does not exist").build());
		}
		return ResponseEntity
				.ok(ApiResponse.builder().success(true).data(product).message("Product fetched successfully").build());
	}

	// Delete a single product by Id
	@DeleteMapping("/products/{id}")
	public ResponseEntity<ApiResponse<Object>> deleteProductById(@PathVariable("id") Long productId) {
		Optional<Product> product = productService.getProductById(productId);
		if (product.isEmpty()) {
			ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(ApiResponse.builder().success(false).data(null).message("Product does not exist").build());
		}
		return ResponseEntity
				.ok(ApiResponse.builder().success(true).data(product).message("Product deleted successfully").build());
	}

	// Returns all products categories
	@GetMapping("/products/categories")
	public ResponseEntity<ApiResponse<Object>> getCategories() {
		List<Category> categories = (List<Category>) categoryService.getCategories();
		return ResponseEntity.ok(ApiResponse.builder().success(true).data(categories).build());
	}

	// Returns welcome message
	@GetMapping("/welcome")
	public String getWelcomeMessage() {
		return "Welcome to Product Service!";
	}

}
