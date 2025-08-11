package vn.edu.iuh.fit.bookshop_be.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.bookshop_be.models.User;
import vn.edu.iuh.fit.bookshop_be.models.UserProfile;
import vn.edu.iuh.fit.bookshop_be.repositories.UserRepository;

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

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean checkPassword(String passwordInput, String password) {
        return passwordEncoder.matches(passwordInput, password);
    }


    public User signUp(User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new IllegalArgumentException("Người dùng đã tồn tại ");
        }
        if(userRepository.findByEmail(user.getEmail()) != null){
            throw new IllegalArgumentException("Email đã tồn tại ");
        }
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        user.setRole("USER");
        user.setCreatedAt(LocalDateTime.now());
        String avatarUrl = avatarService.createAndUploadAvatar(user.getUsername());
        UserProfile userProfile = new UserProfile();
        userProfile.setAvatarUrl(avatarUrl);
        user.setProfile(userProfile);
        User savedUser =  userRepository.save(user);
        return savedUser;
    }

}
