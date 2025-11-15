package vn.edu.iuh.fit.bookshop_be.services;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.bookshop_be.models.Customer;
import vn.edu.iuh.fit.bookshop_be.models.Role;
import vn.edu.iuh.fit.bookshop_be.security.JwtUtil;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class GoogleOAuth2Service {

    private final CustomerService customerService;
    private final JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public GoogleOAuth2Service(CustomerService customerService, JwtUtil jwtUtil) {
        this.customerService = customerService;
        this.jwtUtil = jwtUtil;
    }

    @Value("${google.client.id}")
    private String googleClientId;

    public Customer loginWithGoogle(String token) throws Exception {

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance()
        )
                .setAudience(Collections.singletonList(
                        googleClientId
                ))
                .build();

        GoogleIdToken idToken = verifier.verify(token);

        if (idToken == null) {
            throw new RuntimeException("Token Google không hợp lệ!");
        }

        GoogleIdToken.Payload payload = idToken.getPayload();

        String email = payload.getEmail();
        String name = (String) payload.get("name");
        String picture = (String) payload.get("picture");

        Customer customer = customerService.findByEmail(email);

        // Nếu chưa có → tạo tài khoản mới
        if (customer == null) {
            customer = new Customer();
            String rawPassword = UUID.randomUUID().toString();
            customer.setEmail(email);
            customer.setUsername(name);
            customer.setAvatarUrl(picture);
            customer.setRole(Role.CUSTOMER);
            customer.setCreatedAt(LocalDateTime.now());
            customer.setPasswordHash(passwordEncoder.encode(rawPassword));
            customer.setVerificationCode(UUID.randomUUID().toString());
            customer =  customerService.save(customer);

            customerService.sendVerificationEmail(customer.getEmail(), customer.getVerificationCode());
        } else if(!customer.isEnabled()){
            customerService.sendVerificationEmail(customer.getEmail(), customer.getVerificationCode());
        }



        return customer;
    }
}

