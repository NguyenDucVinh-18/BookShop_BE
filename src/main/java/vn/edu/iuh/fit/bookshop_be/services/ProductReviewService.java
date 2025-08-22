package vn.edu.iuh.fit.bookshop_be.services;

import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.bookshop_be.models.OrderItem;
import vn.edu.iuh.fit.bookshop_be.models.Product;
import vn.edu.iuh.fit.bookshop_be.models.ProductReview;
import vn.edu.iuh.fit.bookshop_be.models.User;
import vn.edu.iuh.fit.bookshop_be.repositories.OrderItemRepository;
import vn.edu.iuh.fit.bookshop_be.repositories.ProductReviewRepository;

import java.util.List;

@Service
public class ProductReviewService {
    private final ProductReviewRepository productReviewRepository;

    public ProductReviewService(ProductReviewRepository productReviewRepository) {
        this.productReviewRepository = productReviewRepository;
    }

    public ProductReview createProductReview(OrderItem orderItem, Integer rating, String comment, User user, Product product, List<String> mediaUrls) {
        ProductReview productReview = new ProductReview();
        productReview.setOrderItem(orderItem);
        productReview.setRating(rating);
        productReview.setComment(comment);
        productReview.setUser(user);
        productReview.setProduct(product);
        productReview.setUserName(user.getUsername());
        productReview.setReviewDate(java.time.LocalDateTime.now());
        productReview.setMediaUrls(mediaUrls);
        orderItem.setReviewed(true);
        return productReviewRepository.save(productReview);
    }

}
