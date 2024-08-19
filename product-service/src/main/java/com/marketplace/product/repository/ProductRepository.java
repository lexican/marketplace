package com.marketplace.product.repository;

import org.springframework.data.repository.CrudRepository;

import com.marketplace.product.models.Product;

public interface ProductRepository extends CrudRepository<Product, Long> {

}