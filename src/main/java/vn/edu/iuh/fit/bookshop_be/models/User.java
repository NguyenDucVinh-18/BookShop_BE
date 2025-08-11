package vn.edu.iuh.fit.bookshop_be.models;


import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JoinColumn(name = "role", nullable = false)
    private String role;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserProfile profile;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Cart cart;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Order> orders;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    private List<Message> sentMessages;

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL)
    private List<Message> receivedMessages;

    public User() {}

    public User(String role, String username, String passwordHash, String email, LocalDateTime createdAt) {
        this.role = role;
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.createdAt = createdAt;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public UserProfile getProfile() { return profile; }
    public void setProfile(UserProfile profile) {
        this.profile = profile;
        if (profile != null) profile.setUser(this);
    }


    public List<Order> getOrders() { return orders; }
    public void setOrders(List<Order> orders) { this.orders = orders; }

    public List<Message> getSentMessages() { return sentMessages; }
    public void setSentMessages(List<Message> sentMessages) { this.sentMessages = sentMessages; }

    public List<Message> getReceivedMessages() { return receivedMessages; }
    public void setReceivedMessages(List<Message> receivedMessages) { this.receivedMessages = receivedMessages; }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }
}

