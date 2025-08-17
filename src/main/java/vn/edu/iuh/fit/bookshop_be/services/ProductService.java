package vn.edu.iuh.fit.bookshop_be.services;

import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.bookshop_be.models.Category;
import vn.edu.iuh.fit.bookshop_be.models.Product;
import vn.edu.iuh.fit.bookshop_be.repositories.ProductRepository;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;


    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product findById(Integer id) {
        return productRepository.findById(id).orElse(null);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Integer id, Product product) {
        if (productRepository.existsById(id)) {
            product.setId(id);
            return productRepository.save(product);
        }
        return null; // or throw an exception
    }

    public void deleteProduct(Integer id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
        }
    }

    public List<Product> findByCategory(Category category) {
        return productRepository.findByCategory(category);
    }

    public List<Product> findByProductType(String productType) {
        return productRepository.findByProductType(productType);
    }








}
