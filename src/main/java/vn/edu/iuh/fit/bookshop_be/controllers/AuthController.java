package vn.edu.iuh.fit.bookshop_be.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.fit.bookshop_be.dtos.LoginRequest;
import vn.edu.iuh.fit.bookshop_be.dtos.SignUpRequest;
import vn.edu.iuh.fit.bookshop_be.models.User;
import vn.edu.iuh.fit.bookshop_be.security.JwtUtil;
import vn.edu.iuh.fit.bookshop_be.services.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/signUp")
    @ResponseBody
    public ResponseEntity<Map<String ,Object>> signup(@RequestBody SignUpRequest request){
//        User user = new User();
//        user.setUsername(request.getUsername());
//        user.setPasswordHash(request.getPassword());
//        user.setEmail(request.getEmail());
        Map<String, Object> response = new HashMap<>();
        try{
            // kiểm tra validation
            if(request.getEmail() == null || request.getUsername() == null || request.getPassword()== null){
                response.put("message", "Điền đầy đủ thông tin");
                return ResponseEntity.status(400).body(response);
            }
            // Gọi UserService để đăng kí user
            userService.signUp(request);
            response.put("message" , "Đăng kí tài khoản thành công");
            response.put("status" , "success");
            Map<String, Object> data = new HashMap<>();
            data.put("user", request);
            response.put("data", data);

            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException e){
            response.put("message" , e.getMessage());
            return ResponseEntity.status(400).body( response);
        } catch (Exception e) {
            response.put("message" , e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }


    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(request.getPassword());
        Map<String, Object> response = new HashMap<>();
        try {
            // kiểm tra username hoặc password có bị null không
            if (user.getEmail() == null || user.getPasswordHash() == null) {
                response.put("message" , "Tài khoản và mật khẩu không được bỏ trống");
                return ResponseEntity.status(400).body(response);
            }
            // kiểm tra user có trong db không
            User existingUser = userService.findByEmail(user.getEmail());

            if (existingUser == null) {
                response.put("message" , "Tài khoản hoặc mật khẩu không chính xác");
                return ResponseEntity.status(401).body(response);
            }
            if (userService.checkPassword(user.getPasswordHash(), existingUser.getPasswordHash())) {
                String accessToken = jwtUtil.generateAccessToken(existingUser.getEmail() , existingUser.getRole().toString());
                String refreshToken = jwtUtil.generateRefreshToken(existingUser.getEmail());
                Map<String, String> tokens = new HashMap<>();
                tokens.put("accessToken", accessToken);
                tokens.put("refreshToken", refreshToken);

                // Tạo map user để chứa thông tin người dùng
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("username", existingUser.getUsername());
                userInfo.put("email", existingUser.getEmail());
                userInfo.put("id", existingUser.getId());
                userInfo.put("avatar", existingUser.getAvatarUrl());
                userInfo.put("role", existingUser.getRole());

                response.put("message", "Đăng nhập thành công");
                response.put("status", "success");
                Map<String, Object> data = new HashMap<>();
                data.put("tokens", tokens);
                data.put("user", userInfo);

                response.put("data", data);


                return ResponseEntity.ok(response);

            } else {
                response.put("message","Tài khoản hoặc mật khẩu không chính xác");
                return ResponseEntity.status(401).body(response);
            }
        } catch (Exception e) {
            response.put("message" , e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/account")
    public ResponseEntity<Map<String, Object>> getUserInfo(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = userService.getUserByToken(authHeader);
            // Kiểm tra xem người dùng có tồn tại không
            if (user == null) {
                response.put("message", "Người dùng không tồn tại");
                return ResponseEntity.status(404).body(response);
            }

            // Trả về thông tin người dùng
//            Map<String, Object> userInfo = new HashMap<>();
            response.put("message", "Lấy thông tin người dùng thành công");
            response.put("status", "success");
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
            response.put("message", "Lỗi hệ thống: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }


}
