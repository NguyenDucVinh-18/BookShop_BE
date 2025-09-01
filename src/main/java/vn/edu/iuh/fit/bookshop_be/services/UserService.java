package vn.edu.iuh.fit.bookshop_be.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.bookshop_be.dtos.SignUpRequest;
import vn.edu.iuh.fit.bookshop_be.dtos.UpdateInfoRequest;
import vn.edu.iuh.fit.bookshop_be.models.Role;
import vn.edu.iuh.fit.bookshop_be.models.User;
import vn.edu.iuh.fit.bookshop_be.repositories.UserRepository;
import vn.edu.iuh.fit.bookshop_be.security.JwtUtil;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

    @Autowired
    private JavaMailSender mailSender;

    @Value("${base_url}")
    private String baseUrl;


    @Value("${spring.mail.username}")
    private String fromEmail;

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
        user.setPhone(request.getPhone());
        user.setEnabled(false);
        user.setVerificationCode(UUID.randomUUID().toString());
        if(userRepository.findByEmail(user.getEmail()) != null){
            throw new IllegalArgumentException("Email đã tồn tại ");
        }
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.now());
        String avatarUrl = avatarService.createAndUploadAvatar(user.getUsername(), user.getEmail());
        user.setAvatarUrl(avatarUrl);
        User savedUser =  userRepository.save(user);
        sendVerificationEmail(user.getEmail(), user.getVerificationCode());
        return savedUser;
    }


    public String sendVerificationEmail(String toEmail, String verificationCode) {
        try {
            // Encode để tránh lỗi ký tự lạ trong code
            String encoded = URLEncoder.encode(verificationCode, StandardCharsets.UTF_8);
            String link = baseUrl + "/api/auth/verify?code=" + encoded;

            // Fallback plain text (phòng khi mail client không hỗ trợ HTML)
            String plainText =
                    "Chào bạn,\n\n" +
                            "Cảm ơn bạn đã đăng ký. Vui lòng xác thực tài khoản để bắt đầu sử dụng dịch vụ.\n\n" +
                            "Nhấp vào liên kết sau: " + link + "\n\n" +
                            "Nếu bạn không thực hiện yêu cầu, hãy bỏ qua email này.";

            // HTML Template với placeholder {{verification_link}}
            String htmlTemplate = """
            <!doctype html>
            <html lang="vi">
            <head>
              <meta charset="utf-8">
              <meta name="viewport" content="width=device-width,initial-scale=1">
              <title>Xác nhận đăng ký</title>
              <style>
                body {
                  background: #f6f8fb;
                  margin: 0;
                  padding: 0;
                  font-family: Inter, Segoe UI, Arial, sans-serif;
                  color: #1f2937;
                }
                .container {
                  max-width: 560px;
                  margin: 0 auto;
                  padding: 24px;
                }
                .card {
                  background: #ffffff;
                  border-radius: 16px;
                  box-shadow: 0 6px 18px rgba(0,0,0,.06);
                  overflow: hidden;
                }
                .header {
                  background: linear-gradient(135deg, #4f46e5, #06b6d4);
                  padding: 24px;
                  color: #fff !important;
                }
                .brand {
                  font-size: 18px;
                  font-weight: 700;
                  letter-spacing: .4px;
                  color: #ffffff !important;
                }
                .content {
                  padding: 24px;
                }
                .title {
                  font-size: 20px;
                  margin: 0 0 8px;
                  font-weight: bold;
                }
                .muted {
                  color: #6b7280;
                  margin: 0 0 20px;
                  line-height: 1.6;
                }
                .btn {
                  display: inline-block;
                  text-decoration: none;
                  padding: 12px 18px;
                  border-radius: 10px;
                  background: #4f46e5;
                  color: #fff !important;
                  font-weight: 600;
                }
                .btn:hover {
                  opacity: .95;
                }
                .link {
                  word-break: break-all;
                  font-size: 12px;
                  color: #2563eb;
                  margin-top: 12px;
                }
                .footer {
                  padding: 16px 24px;
                  border-top: 1px solid #eef2f7;
                  color: #9ca3af;
                  font-size: 12px;
                }
              </style>
            </head>
            <body>
              <div class="container">
                <div class="card">
                  <!-- HEADER -->
                  <div class="header">
                    <div class="brand">📚 BookShop • Xác thực tài khoản</div>
                  </div>

                  <!-- CONTENT -->
                  <div class="content">
                    <h1 class="title">Chào bạn,</h1>
                    <p class="muted">
                      Cảm ơn bạn đã đăng ký. Vui lòng xác thực tài khoản để bắt đầu sử dụng dịch vụ.
                    </p>
                    <p style="margin:16px 0 24px">
                      <a class="btn" href="{{verification_link}}" target="_blank" rel="noopener">
                        Xác nhận tài khoản
                      </a>
                    </p>
                  </div>

                  <!-- FOOTER -->
                  <div class="footer">
                    Email này được gửi tự động, vui lòng không trả lời.<br>
                    Nếu bạn không thực hiện yêu cầu, hãy bỏ qua email.
                  </div>
                </div>
              </div>
            </body>
            </html>
            """;

            // Thay thế placeholder
            String html = htmlTemplate.replace("{{verification_link}}", link);

            // Tạo mail MIME để gửi kèm HTML
            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    mime,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );

            helper.setTo(toEmail);
            helper.setSubject("Xác nhận đăng ký tài khoản");
            if (fromEmail != null && !fromEmail.isBlank()) {
                helper.setFrom(fromEmail);
            }

            // setText(plain, html) => ưu tiên HTML, fallback text
            helper.setText(plainText, html);

            mailSender.send(mime);

            return verificationCode;
        } catch (MessagingException e) {
            throw new RuntimeException("Không thể gửi email xác thực: " + e.getMessage(), e);
        }
    }

    public boolean verifyUser(String verificationCode) {
        User user = userRepository.findByVerificationCode(verificationCode);;
        if (user.isEnabled()) {
            return false;
        }
        user.setEnabled(true);
        user.setVerificationCode(null); // Xóa mã xác thực
        userRepository.save(user);
        return true;
    }

    public User findByVerificationCode(String code) {
        return userRepository.findByVerificationCode(code);
    }





}
