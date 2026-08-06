package com.library.management.service;

import java.util.List;

import com.library.management.entity.Category;

public interface CategoryService {
    List<Category> getAllCategories();
    Category getCategoryById(Integer id);
    Category getCategoryByName(String name);
    Category createCategory(Category category);
    Category updateCategory(Integer id, Category categoryDetails);
    void deleteCategory(Integer id);
}