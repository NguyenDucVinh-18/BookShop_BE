package vn.edu.iuh.fit.bookshop_be.controllers;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.iuh.fit.bookshop_be.models.User;
import vn.edu.iuh.fit.bookshop_be.security.JwtUtil;
import vn.edu.iuh.fit.bookshop_be.services.UserService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final Cloudinary cloudinary;

    public UserController(UserService userService, JwtUtil jwtUtil, Cloudinary cloudinary) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.cloudinary = cloudinary;
    }

    @PostMapping("/updateAvatar/{userId}")
    public ResponseEntity<Map<String, Object>> updateAvatar(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("userId") Integer id,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = userService.getUserByToken(authHeader);

            if (user == null || !user.getId().equals(id)) {
                response.put("status", "error");
                response.put("message", "Bạn không có quyền cập nhật ảnh đại diện của người dùng này");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            // Kiểm tra có gửi ảnh không
            if (image == null || image.isEmpty()) {
                response.put("status", "error");
                response.put("message", "Vui lòng chọn ảnh để cập nhật");
                return ResponseEntity.badRequest().body(response);
            }

            if (user == null) {
                response.put("status", "error");
                response.put("message", "Không tìm thấy người dùng");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Upload ảnh lên Cloudinary
            Map uploadResult = cloudinary.uploader().upload(image.getBytes(),
                    ObjectUtils.asMap("folder", "avatars"));

            String imageUrl = (String) uploadResult.get("secure_url");

            // Cập nhật avatar vào user
            user.setAvatarUrl(imageUrl);
            userService.save(user);

            // Trả kết quả
            response.put("status", "success");
            response.put("message", "Cập nhật ảnh đại diện thành công");
            User userRender = new User();
            userRender.setId(user.getId());
            userRender.setUsername(user.getUsername());
            userRender.setEmail(user.getEmail());
            userRender.setRole(user.getRole());
            userRender.setAvatarUrl(user.getAvatarUrl());

            Map<String, Object> data = new HashMap<>();
            data.put("user", userRender);
            response.put("data", data);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Lỗi khi cập nhật ảnh: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }

}
