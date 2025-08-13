package vn.edu.iuh.fit.bookshop_be.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.bookshop_be.dtos.SignUpRequest;
import vn.edu.iuh.fit.bookshop_be.dtos.UpdateInfoRequest;
import vn.edu.iuh.fit.bookshop_be.models.User;
import vn.edu.iuh.fit.bookshop_be.repositories.UserRepository;
import vn.edu.iuh.fit.bookshop_be.security.JwtUtil;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AvatarService avatarService;

    @Autowired
    private JwtUtil jwtUtil;

    public User save(User user) {
        return userRepository.save(user);
    }

    public User findById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean checkPassword(String passwordInput, String password) {
        return passwordEncoder.matches(passwordInput, password);
    }

    public User getUserByToken(String authHeader) {
        try {
            // Kiểm tra xem header có đúng định dạng không
            if (authHeader == null) {
                return null;
            }
            String token = null;
            if(authHeader.startsWith("Bearer ")){
                token = authHeader.substring(7); // Bỏ qua "Bearer "
            }
            else {
                token = authHeader;
            }

            // Trích xuất username từ token
            String email = jwtUtil.extractEmail(token);
            if (email == null) {
                return null;
            }

            // Tìm người dùng từ database
            User user = userRepository.findByEmail(email);
            return user;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public User signUp(SignUpRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(request.getPassword());
        user.setEmail(request.getEmail());
        if(userRepository.findByEmail(user.getEmail()) != null){
            throw new IllegalArgumentException("Email đã tồn tại ");
        }
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        user.setRole("USER");
        user.setCreatedAt(LocalDateTime.now());
        String avatarUrl = avatarService.createAndUploadAvatar(user.getUsername(), user.getEmail());
        user.setAvatarUrl(avatarUrl);
        User savedUser =  userRepository.save(user);
        return savedUser;
    }



}
