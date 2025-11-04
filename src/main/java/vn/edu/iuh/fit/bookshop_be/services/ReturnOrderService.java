package vn.edu.iuh.fit.bookshop_be.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.bookshop_be.dtos.ProductStockReceiptRequest;
import vn.edu.iuh.fit.bookshop_be.models.*;
import vn.edu.iuh.fit.bookshop_be.repositories.InventoryRepository;
import vn.edu.iuh.fit.bookshop_be.repositories.ReturnOrderRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReturnOrderService {
    private final ReturnOrderRepository returnOrderRepository;
    private final OrderService orderService;
    private final VNPayService vnPayService;
    private final StockReceiptService stockReceiptService;
    private final InventoryRepository inventoryRepository;
    private final ProductService productService;
    private final ChatSocketService chatSocketService;
    private final CustomerService customerService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;



    public ReturnOrderService(ReturnOrderRepository returnOrderRepository, OrderService orderService, VNPayService vnPayService, StockReceiptService stockReceiptService, InventoryRepository inventoryRepository, ProductService productService, ChatSocketService chatSocketService, CustomerService customerService) {
        this.returnOrderRepository = returnOrderRepository;
        this.orderService = orderService;
        this.vnPayService = vnPayService;
        this.stockReceiptService = stockReceiptService;
        this.inventoryRepository = inventoryRepository;
        this.productService = productService;
        this.chatSocketService = chatSocketService;
        this.customerService = customerService;
    }

    public ReturnOrder save(ReturnOrder returnOrder) {
        return returnOrderRepository.save(returnOrder);
    }

    public ReturnOrder createReturnOrder(Order order, String reason, String note) {
        ReturnOrder returnOrder = new ReturnOrder();
        returnOrder.setOrder(order);
        returnOrder.setNote(note);
        returnOrder.setReason(reason);
        returnOrder.setRefundAmount(order.getTotalAmount());
        returnOrder.setRequestDate(LocalDateTime.now());
        returnOrder.setStatus(ReturnOrderStatus.PENDING);

        return returnOrderRepository.save(returnOrder);
    }

    public List<ReturnOrder> getAll() {
        return returnOrderRepository.findAll(Sort.by(Sort.Direction.DESC, "requestDate"));
    }

    public ReturnOrder getById(Integer id) {
        return returnOrderRepository.findById(id).orElse(null);
    }



    public ReturnOrder updateStatus(Employee employee, ReturnOrder returnOrder, ReturnOrderStatus status) throws IOException {
        returnOrder.setStatus(status);
        returnOrder.setEmployee(employee);

        Order order = orderService.findById(returnOrder.getOrder().getId());
        System.out.println("order: " + order.getId());
        Customer customer = customerService.findByOrder(order);

        String message = "";

        if (status == ReturnOrderStatus.APPROVED) {
            returnOrder.setProcessedDate(LocalDateTime.now());
            order.setStatus(OrderStatus.REFUNDING);
            orderService.save(order);

            message = "Yêu cầu hoàn trả đơn hàng " + order.getOrderCode() +
                    " đã được chấp nhận. Vui lòng chuẩn bị hàng và gửi lại cho shop để kiểm tra. Sau khi kiểm tra xong, shop sẽ tiến hành hoàn tiền.";
        } else if (status == ReturnOrderStatus.COMPLETED) {
            order.setStatus(OrderStatus.REFUNDED);
            orderService.save(order);

            if(order.getPaymentMethod() != PaymentMethod.BANKING) {
                String transDate = order.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                String transactionType = "02"; // Hoan tien
                String result = vnPayService.refundVNPay(
                        order.getPaymentRef(),
                        order.getTotalAmount().longValue(),
                        transDate,
                        "adminRefund",
                        transactionType
                );
            }

            List<ProductStockReceiptRequest> productStockReceiptRequests = new ArrayList<>();
            for(OrderItem item : order.getOrderItems()) {
                ProductStockReceiptRequest request = new ProductStockReceiptRequest();
                request.setProductId(item.getProduct().getId());
                request.setQuantity(item.getQuantity());
                productStockReceiptRequests.add(request);
            }
            stockReceiptService.save(
                    "Nhập kho do trả hàng : " + order.getOrderCode(),
                    TypeStockReceipt.IMPORT,
                    null,
                    "Đã nhập kho do hoàn trả từ đơn hàng : " + order.getOrderCode(),
                    productStockReceiptRequests
            );

//            for(ProductStockReceiptRequest request : productStockReceiptRequests) {
//                Inventory inventory = inventoryRepository.findByProduct(productService.findById(request.getProductId())).orElse(null);
//                if (inventory != null) {
//                    inventory.setAvailableQuantity(inventory.getAvailableQuantity() + request.getQuantity());
//                    inventory.setActualQuantity(inventory.getActualQuantity() + request.getQuantity());
//                    inventory.recalculateAvailable();
//                    inventoryRepository.save(inventory);
//                }
//            }

            message = "Shop đã nhận và kiểm tra hàng hoàn trả của bạn cho đơn " + order.getOrderCode() +
                    ". Tiền sẽ được hoàn lại trong thời gian sớm nhất. Cảm ơn bạn đã mua sắm tại cửa hàng!";

        } else if (status == ReturnOrderStatus.REJECTED) {
            order.setStatus(OrderStatus.REFUND_REJECTED);
            orderService.save(order);

            message = "Yêu cầu hoàn trả cho đơn hàng " + order.getOrderCode() +
                    " đã bị từ chối. Vui lòng liên hệ bộ phận hỗ trợ để biết thêm chi tiết.";
        }

        // ✅ Gửi tin nhắn tự động cho khách hàng
        System.out.println("Sending message to customer: " + message);
        System.out.println("Customer: " + customer.getId());
        System.out.println("Employee: " + employee.getId());
        if (!message.isEmpty() && customer != null) {
            Message savedMsg = chatSocketService.sendMessage(Role.STAFF, employee.getId(), customer.getId(), message);
            messagingTemplate.convertAndSend("/topic/messages/" + customer.getId(), savedMsg);

            // Cập nhật lại danh sách conversation (nếu cần)
            List<Conversation> allConversations = chatSocketService.getConversations();
            messagingTemplate.convertAndSend("/topic/conversations", allConversations);
        }

        return returnOrderRepository.save(returnOrder);
    }

}
