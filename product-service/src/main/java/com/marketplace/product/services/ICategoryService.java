package com.marketplace.product.services;

import java.util.List;
import java.util.Optional;

import com.marketplace.product.models.Category;

public interface ICategoryService {

	List<Category> getCategories();

	Optional<Category> getCategoryById(Long categoryId);

}
