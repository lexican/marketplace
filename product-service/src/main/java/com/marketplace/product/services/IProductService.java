package com.marketplace.product.services;

import java.util.List;
import java.util.Optional;

import com.marketplace.product.models.Product;

public interface IProductService {
	List<Product> getProducts();

	Optional<Product> getProductById(Long productId);

	Optional<Product> deleteProductById(Long productId);

	Product createProduct(Product product);
}
