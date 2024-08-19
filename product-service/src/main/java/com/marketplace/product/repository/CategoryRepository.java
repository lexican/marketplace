package com.marketplace.product.repository;

import org.springframework.data.repository.CrudRepository;

import com.marketplace.product.models.Category;

public interface CategoryRepository extends CrudRepository<Category, Long> {

}
