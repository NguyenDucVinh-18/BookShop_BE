//package vn.edu.iuh.fit.bookshop_be.models;
//
//import jakarta.persistence.*;
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "messages")
//public class Message {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer id;
//
//    @ManyToOne
//    @JoinColumn(name = "sender_id", nullable = false)
//    private Customer sender;
//
//    @ManyToOne
//    @JoinColumn(name = "receiver_id", nullable = false)
//    private Customer receiver;
//
//    @Column(columnDefinition = "TEXT", nullable = false)
//    private String content;
//
//    @Column(name = "created_at")
//    private LocalDateTime createdAt;
//
//    public Message() {}
//
//    public Message(Customer sender, Customer receiver, String content, LocalDateTime createdAt) {
//        this.sender = sender;
//        this.receiver = receiver;
//        this.content = content;
//        this.createdAt = createdAt;
//    }
//
//    public Integer getId() { return id; }
//    public void setId(Integer id) { this.id = id; }
//
//    public Customer getSender() { return sender; }
//    public void setSender(Customer sender) { this.sender = sender; }
//
//    public Customer getReceiver() { return receiver; }
//    public void setReceiver(Customer receiver) { this.receiver = receiver; }
//
//    public String getContent() { return content; }
//    public void setContent(String content) { this.content = content; }
//
//    public LocalDateTime getCreatedAt() { return createdAt; }
//    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
//}
