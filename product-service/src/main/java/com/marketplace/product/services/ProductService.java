package com.marketplace.product.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.marketplace.product.models.Product;
import com.marketplace.product.repository.ProductRepository;

@Service
public class ProductService implements IProductService {
	private ProductRepository productRepository;
	
	ProductService(ProductRepository productRepository){
		this.productRepository = productRepository;
	}
	@Override
	public List<Product> getProducts() {
	
		return (List<Product>) productRepository.findAll();
	}

}