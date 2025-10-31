package vn.edu.iuh.fit.bookshop_be.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import vn.edu.iuh.fit.bookshop_be.dtos.SendMessageRequest;
import vn.edu.iuh.fit.bookshop_be.models.Conversation;
import vn.edu.iuh.fit.bookshop_be.models.Message;
import vn.edu.iuh.fit.bookshop_be.models.Role;
import vn.edu.iuh.fit.bookshop_be.services.ChatSocketService;
import vn.edu.iuh.fit.bookshop_be.services.CustomerService;
import vn.edu.iuh.fit.bookshop_be.services.EmployeeService;

import java.util.List;

@Controller
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

            // 🔄 Cập nhật danh sách conversation cho nhân viên
            List<Conversation> allConversations = chatSocketService.getConversations();
            messagingTemplate.convertAndSend("/topic/conversations", allConversations);

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
}


