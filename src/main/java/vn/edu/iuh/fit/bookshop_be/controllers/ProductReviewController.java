package vn.edu.iuh.fit.bookshop_be.controllers;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.iuh.fit.bookshop_be.dtos.ReviewProductRequest;
import vn.edu.iuh.fit.bookshop_be.models.OrderItem;
import vn.edu.iuh.fit.bookshop_be.models.Product;
import vn.edu.iuh.fit.bookshop_be.models.ProductReview;
import vn.edu.iuh.fit.bookshop_be.models.User;
import vn.edu.iuh.fit.bookshop_be.services.OrderItemService;
import vn.edu.iuh.fit.bookshop_be.services.ProductReviewService;
import vn.edu.iuh.fit.bookshop_be.services.ProductService;
import vn.edu.iuh.fit.bookshop_be.services.UserService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/product-review")
public class ProductReviewController {
    private final ProductReviewService productReviewService;
    private final UserService userService;
    private final ProductService productService;
    private final OrderItemService orderItemService;
    private final Cloudinary cloudinary;

    public ProductReviewController(ProductReviewService productReviewService, UserService userService, ProductService productService, OrderItemService orderItemService, Cloudinary cloudinary) {
        this.productReviewService = productReviewService;
        this.userService = userService;
        this.productService = productService;
        this.orderItemService = orderItemService;
        this.cloudinary = cloudinary;
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> deletePromotion(
            @RequestHeader("Authorization") String authHeader,
            @RequestPart("request") ReviewProductRequest request,
            @RequestPart(value = "medias", required = false) MultipartFile[] mediaFiles
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = userService.getUserByToken(authHeader);
            if (user == null) {
                response.put("status", "error");
                response.put("message", "Bạn cần đăng nhập để viết đánh giá sản phẩm");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Kiểm tra thông tin đánh giá
            if (request.getOrderItemId() == null || request.getRating() == null || request.getComment() == null) {
                response.put("status", "error");
                response.put("message", "Thông tin đánh giá không đầy đủ");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Kiểm tra rating
            if (request.getRating() < 1 || request.getRating() > 5) {
                response.put("status", "error");
                response.put("message", "Đánh giá phải từ 1 đến 5 sao");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            OrderItem orderItem = orderItemService.findById(request.getOrderItemId());
            if (orderItem == null) {
                response.put("status", "error");
                response.put("message", "Đơn hàng không tồn tại");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Product product = productService.findById(orderItem.getProduct().getId());
            if (product == null) {
                response.put("status", "error");
                response.put("message", "Sản phẩm không tồn tại");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }


            // Xử lý tải lên ảnh và video
            List<String> mediaUrls = new ArrayList<>();
            if (mediaFiles != null ) {
                for (MultipartFile file : mediaFiles) {
                    if (file != null && !file.isEmpty()) {
                        // Kiểm tra định dạng tệp (chỉ cho phép ảnh và video)
                        String contentType = file.getContentType();
                        if (contentType != null && (contentType.startsWith("image/") || contentType.startsWith("video/"))) {
                            try {
                                Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                                        ObjectUtils.asMap(
                                                "folder", "product_reviews",
                                                "resource_type", contentType.startsWith("video/") ? "video" : "image"
                                        ));
                                String mediaUrl = (String) uploadResult.get("secure_url");
                                mediaUrls.add(mediaUrl);
                            } catch (IOException e) {
                                response.put("status", "error");
                                response.put("message", "Lỗi khi tải tệp lên Cloudinary: " + e.getMessage());
                                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                            }
                        } else {
                            response.put("status", "error");
                            response.put("message", "Định dạng tệp không hợp lệ. Chỉ chấp nhận ảnh (PNG, JPG) hoặc video (MP4).");
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                        }
                    }
                }
            }

            ProductReview productReview =  productReviewService.createProductReview(
                   orderItem,
                    request.getRating(),
                    request.getComment(),
                    user,
                    product,
                    mediaUrls
            );


            orderItem.setReviewed(true);


            response.put("status", "success");
            response.put("message", "Viết đánh giá sản phẩm thành công");
            Map<String, Object> data = new HashMap<>();
            data.put("review", productReview);
            response.put("data", data);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Lỗi khi viết đánh giá sản phẩm: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
