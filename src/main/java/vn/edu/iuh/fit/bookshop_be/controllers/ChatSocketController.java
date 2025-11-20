package vn.edu.iuh.fit.bookshop_be.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.iuh.fit.bookshop_be.dtos.SendMessageRequest;
import vn.edu.iuh.fit.bookshop_be.models.Conversation;
import vn.edu.iuh.fit.bookshop_be.models.Message;
import vn.edu.iuh.fit.bookshop_be.models.Role;
import vn.edu.iuh.fit.bookshop_be.services.ChatSocketService;
import vn.edu.iuh.fit.bookshop_be.services.CustomerService;
import vn.edu.iuh.fit.bookshop_be.services.EmployeeService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/chat")
public class ChatSocketController {

    private final CustomerService customerService;
    private final EmployeeService employeeService;
    private final ChatSocketService chatSocketService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public ChatSocketController(CustomerService customerService,
                                EmployeeService employeeService,
                                ChatSocketService chatSocketService) {
        this.customerService = customerService;
        this.employeeService = employeeService;
        this.chatSocketService = chatSocketService;
    }

    /**
     * 📩 Gửi tin nhắn realtime (tách riêng theo customerId)
     */
    @MessageMapping("/sendMessage")
    public void sendMessage(@Payload SendMessageRequest request) {
        try {
            String message = request.getMessage();
            Integer customerId = request.getCustomerId();
            Integer senderId = request.getSenderId();
            Role role = request.getSenderRole();

            Message savedMsg = chatSocketService.sendMessage(role, senderId, customerId, message);

            // 🔊 Chỉ gửi cho đúng cuộc trò chuyện (customerId)
            messagingTemplate.convertAndSend("/topic/messages/" + customerId, savedMsg);

            Conversation conversation = chatSocketService.findConversationByCustomerId(customerId);

            // 🔄 Cập nhật danh sách conversation cho nhân viên
            List<Conversation> allConversations = chatSocketService.getConversations();
            messagingTemplate.convertAndSend("/topic/conversations", allConversations);

            if(conversation == null) {
                messagingTemplate.convertAndSend("/topic/customer/unread/" + customerId, 0);
                return;
            }
            int unreadCount = conversation.getUnreadCount();
            messagingTemplate.convertAndSend("/topic/customer/unread/" + customerId, unreadCount);

        } catch (Exception e) {
            System.err.println("❌ Error sending message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 🕓 Lấy lịch sử tin nhắn theo customerId
     */
    @MessageMapping("/getMessages")
    public void getMessages(@Payload Integer customerId) {
        try {
            List<Message> list = chatSocketService.findMessagesByCustomerId(customerId);
            messagingTemplate.convertAndSend("/topic/history/" + customerId, list);
        } catch (Exception e) {
            System.err.println("❌ Error getting messages: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 💬 Lấy danh sách tất cả cuộc trò chuyện (cho nhân viên)
     */
    @MessageMapping("/getConversations")
    public void getConversations() {
        try {
            List<Conversation> list = chatSocketService.getConversations();
            messagingTemplate.convertAndSend("/topic/conversations", list);
        } catch (Exception e) {
            System.err.println("❌ Error getting conversations: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @MessageMapping("/getUnread")
    public void getUnread(@Payload Integer customerId) {
        try {
            Conversation conversation = chatSocketService.findConversationByCustomerId(customerId);
            if(conversation == null) {
                messagingTemplate.convertAndSend("/topic/customer/unread/" + customerId, 0);
                return;
            }
            int unread = conversation.getUnreadCount();
            messagingTemplate.convertAndSend("/topic/customer/unread/" + customerId, unread);

        } catch (Exception e) {
            System.err.println("❌ Error getting unread messages: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 📝 Đánh dấu tất cả tin nhắn của customerId là đã đọc (unread = 0)
     */
    @PostMapping("/readAll/{customerId}")
    public ResponseEntity<Map<String, Object>> markAllAsRead(@PathVariable Integer customerId) {
        Map<String, Object> response = new HashMap<>();
        try {
            chatSocketService.markMessagesAsRead(customerId);
            messagingTemplate.convertAndSend(
                    "/topic/customer/unread/" + customerId,
                    0
            );

            response.put("status", "success");
            response.put("message", "Đã đánh dấu tất cả tin nhắn là đã đọc.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Lỗi khi đọc tin nhắn: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}


