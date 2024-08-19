package com.marketplace.product.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.marketplace.product.models.Category;
import com.marketplace.product.repository.CategoryRepository;

@Service
public class CategoryService implements ICategoryService {
	private CategoryRepository categoryRepository;

	CategoryService(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	@Override
	public List<Category> getCategories() {

		return (List<Category>) categoryRepository.findAll();
	}

}