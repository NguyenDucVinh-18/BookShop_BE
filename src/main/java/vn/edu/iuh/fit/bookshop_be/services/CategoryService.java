package vn.edu.iuh.fit.bookshop_be.services;

import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.bookshop_be.models.Category;
import vn.edu.iuh.fit.bookshop_be.repositories.CategoryRepository;

import java.util.List;

@Service
public class CategoryService{
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories() {

        return categoryRepository.findAll();
    }

    public Category getCategoryById(Integer id) {
        return categoryRepository.findById(id).orElse(null);
    }

    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    public Category updateCategory(Integer id, Category category) {
        if (categoryRepository.existsById(id)) {
            category.setId(id);
            return categoryRepository.save(category);
        }
        return null; // or throw an exception
    }

    public void deleteCategory(Integer id) {
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
        }
    }

    public Category findById(Integer id) {
        return categoryRepository.findById(id).orElse(null);
    }

    public List<Category> searchCategories(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return getAllCategories();
        }
        return categoryRepository.findAll().stream()
                .filter(category -> category.getName().toLowerCase().contains(keyword.toLowerCase()))
                .toList();
    }

    public List<Category> getCategoriesByProductId(Integer productId) {
        return categoryRepository.findAll().stream()
                .filter(category -> category.getProducts().stream()
                        .anyMatch(product -> product.getId().equals(productId)))
                .toList();
    }

    public List<Category> getCategoriesByName(String name) {
        return categoryRepository.findAll().stream()
                .filter(category -> category.getName().equalsIgnoreCase(name))
                .toList();
    }

}
