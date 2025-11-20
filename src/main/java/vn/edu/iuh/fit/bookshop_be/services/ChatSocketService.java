package vn.edu.iuh.fit.bookshop_be.services;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.bookshop_be.models.Conversation;
import vn.edu.iuh.fit.bookshop_be.models.Customer;
import vn.edu.iuh.fit.bookshop_be.models.Message;
import vn.edu.iuh.fit.bookshop_be.models.Role;
import vn.edu.iuh.fit.bookshop_be.repositories.ConversationRepository;
import vn.edu.iuh.fit.bookshop_be.repositories.MessageRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatSocketService {
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final CustomerService customerService;
    private final EmployeeService employeeService;

    public ChatSocketService(ConversationRepository conversationRepository, MessageRepository messageRepository, CustomerService customerService, EmployeeService employeeService) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.customerService = customerService;
        this.employeeService = employeeService;
    }

    public Conversation save (Conversation conversation) {
        return conversationRepository.save(conversation);
    }

    public Conversation findConversationByCustomerId(Integer customerId) {
        Customer customer = customerService.findById(customerId);
        return conversationRepository.findByCustomer(customer);
    }

    public Message sendMessage(Role roleRender, Integer idRender, Integer customerId, String message) {
        Conversation conversation = findConversationByCustomerId(customerId);
        if (conversation == null) {
            Conversation newConversation = new Conversation();
            Customer customer = customerService.findById(idRender);
            newConversation.setCustomer(customer);
            newConversation.setUnreadCount(0);
            conversation = conversationRepository.save(newConversation);
        }
        conversation.setLastMessage(message);
        conversation.setUpdatedAt(LocalDateTime.now());
        conversationRepository.save(conversation);
        Message newMessage = new Message();
        newMessage.setConversation(conversation);
        newMessage.setMessage(message);
        newMessage.setRead(false);
        newMessage.setCreatedAt(LocalDateTime.now());
        if (roleRender == Role.CUSTOMER) {
            newMessage.setSentByCustomer(true);
        } else {
            newMessage.setSentByCustomer(false);
            newMessage.setEmployee(employeeService.findById(idRender));
            conversation.setUnreadCount(conversation.getUnreadCount() + 1);
            conversationRepository.save(conversation);
        }
        return messageRepository.save(newMessage);
    }



    public List<Message> findMessagesByConversationId(Integer conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId).orElse(null);
        if (conversation == null) return List.of();
        return messageRepository.findByConversation(conversation);
    }


    public List<Message> findMessagesByCustomerId(Integer customerId) {
        Conversation conversation = findConversationByCustomerId(customerId);
        if (conversation == null) {
            return null;
        }
        List<Message> messages = messageRepository.findByConversation(conversation);
        return messages;
    }

    public List<Conversation> getConversations() {
        return conversationRepository.findAll(Sort.by(Sort.Direction.DESC, "updatedAt"));
    }

    public int getUnreadCount(Integer customerId) {
        Conversation conversation = findConversationByCustomerId(customerId);
        if (conversation == null) {
            return 0;
        }
        return conversation.getUnreadCount();
    }

    public void markMessagesAsRead(Integer customerId) {
        Conversation conversation = findConversationByCustomerId(customerId);
        if (conversation == null) {
            return;
        }
        conversation.setUnreadCount(0);
        conversationRepository.save(conversation);
    }


}
