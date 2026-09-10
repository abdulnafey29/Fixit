package com.fixit.service;

import com.fixit.dto.request.CategoryRequest;
import com.fixit.entity.Category;

import java.util.List;

public interface CategoryService {
    List<Category> getAllCategories();
    Category getCategoryById(Long id);
    Category createCategory(CategoryRequest request);
    void deleteCategory(Long id);
}
