package vn.edu.iuh.fit.bookshop_be.controllers;


import com.cloudinary.utils.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.iuh.fit.bookshop_be.dtos.CategoryRequest;
import vn.edu.iuh.fit.bookshop_be.models.Category;
import vn.edu.iuh.fit.bookshop_be.models.User;
import vn.edu.iuh.fit.bookshop_be.services.CategoryService;
import vn.edu.iuh.fit.bookshop_be.services.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/category")
public class CategoryController {
    private final CategoryService categoryService;
    private final UserService userService;

    public CategoryController(CategoryService categoryService, UserService userService) {
        this.categoryService = categoryService;
        this.userService = userService;
    }

    /**
     * Tạo mới danh mục
     * @param authHeader
     * @param request
     * @return ResponseEntity với thông tin về danh mục mới được tạo
     */
    @PostMapping("/createCategory")
    public ResponseEntity<Map<String, Object>> createCategory(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody CategoryRequest request
    ) {
        Map<String, Object> response = new HashMap<>();
        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        try {
            User user = userService.getUserByToken(authHeader);

            if (user == null ) {
                response.put("status", "error");
                response.put("message", "Bạn cần đăng nhập để tạo danh mục");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            if (user.getRole() == null || !user.getRole().equals("ADMIN")) {
                response.put("status", "error");
                response.put("message", "Bạn không có quyền tạo danh mục");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            if (request.getName() == null || request.getName().isEmpty()) {
                response.put("status", "error");
                response.put("message", "Tên danh mục không được để trống");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            categoryService.save(category);

            response.put("status", "success");
            response.put("message", "Tạo danh mục thành công");
            Map<String, Object> data = new HashMap<>();
            data.put("category", category);
            response.put("data", data);


            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Lỗi khi cập nhật ảnh: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Lấy danh sách tất cả danh mục
     * @param authHeader
     * @return ResponseEntity với danh sách danh mục
     */
    @GetMapping("/getAllCategories")
    public ResponseEntity<Map<String, Object>> getAllCategories(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = userService.getUserByToken(authHeader);

            if (user == null ) {
                response.put("status", "error");
                response.put("message", "Bạn cần đăng nhập để lấy danh sách danh mục");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            if (user.getRole() == null || !user.getRole().equals("ADMIN")) {
                response.put("status", "error");
                response.put("message", "Bạn không có quyền lấy danh sách danh mục");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            response.put("status", "success");
            response.put("message", "Lấy danh sách danh mục thành công");
            Map<String, Object> data = new HashMap<>();
            List<Category> categories = categoryService.getAllCategories();
            List<Category> categorireRender = categories.stream()
                    .map(category -> {
                        Category categoryRender = new Category();
                        categoryRender.setId(category.getId());
                        categoryRender.setName(category.getName());
                        categoryRender.setDescription(category.getDescription());
                        return categoryRender;
                    }).toList();
            data.put("categories",categorireRender);
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Lỗi khi lấy danh sách danh mục: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Cập nhật danh mục theo ID
     * @param authHeader
     * @param id
     * @return ResponseEntity với thông tin về danh mục đã cập nhật
     */
    @PutMapping("/updateCategory/{id}")
    public ResponseEntity<Map<String, Object>> updateCategory(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("id") Integer id,
            @RequestBody CategoryRequest request
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = userService.getUserByToken(authHeader);

            if (user == null ) {
                response.put("status", "error");
                response.put("message", "Bạn cần đăng nhập để cập nhật danh mục");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            if (user.getRole() == null || !user.getRole().equals("ADMIN")) {
                response.put("status", "error");
                response.put("message", "Bạn không có quyền cập nhật danh mục");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            Category category = categoryService.getCategoryById(id);
            if (category == null) {
                response.put("status", "error");
                response.put("message", "Danh mục không tồn tại");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            if (request.getName() == null || request.getName().isEmpty()) {
                response.put("status", "error");
                response.put("message", "Tên danh mục không được để trống");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            category.setName(request.getName());
            category.setDescription(request.getDescription());
            categoryService.updateCategory(id,category);

            response.put("status", "success");
            response.put("message", "Cập nhật danh mục thành công");
            Map<String, Object> data = new HashMap<>();
            Category categoryRender = new Category();
            categoryRender.setId(category.getId());
            categoryRender.setName(category.getName());
            categoryRender.setDescription(category.getDescription());
            data.put("category", categoryRender);
            response.put("data", data);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Lỗi khi cập nhật danh mục: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Xóa danh mục theo ID
     * @param authHeader
     * @param id
     * @return ResponseEntity với thông tin về việc xóa danh mục
     */
    @DeleteMapping("/deleteCategory/{id}")
    public ResponseEntity<Map<String, Object>> deleteCategory(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("id") Integer id
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = userService.getUserByToken(authHeader);

            if (user == null ) {
                response.put("status", "error");
                response.put("message", "Bạn cần đăng nhập để xóa danh mục");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            if (user.getRole() == null || !user.getRole().equals("ADMIN")) {
                response.put("status", "error");
                response.put("message", "Bạn không có quyền xóa danh mục");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            Category category = categoryService.getCategoryById(id);
            if (category == null) {
                response.put("status", "error");
                response.put("message", "Danh mục không tồn tại");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            categoryService.deleteCategory(id);

            response.put("status", "success");
            response.put("message", "Xóa danh mục thành công");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Lỗi khi xóa danh mục: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


}
