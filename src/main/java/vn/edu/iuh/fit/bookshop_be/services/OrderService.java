package vn.edu.iuh.fit.bookshop_be.services;

import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.bookshop_be.dtos.ProductOrderRequest;
import vn.edu.iuh.fit.bookshop_be.models.*;
import vn.edu.iuh.fit.bookshop_be.repositories.OrderRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductService productService;

    public OrderService(OrderRepository orderRepository, ProductService productService) {
        this.orderRepository = orderRepository;
        this.productService = productService;
    }

    public Order save(Order order) {
        return orderRepository.save(order);
    }

    public Order placeOrder(User user, PaymentMethod paymentMethod, String address, String phone ,String note, List<ProductOrderRequest> productOrderRequests) {
        Order order = new Order();
        order.setUser(user);
        order.setPaymentMethod(paymentMethod);
        order.setAddress(address);
        order.setPhone(phone);
        order.setNote(note);
        order.setCreatedAt(LocalDateTime.now());
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (ProductOrderRequest request : productOrderRequests) {
            OrderItem orderItem = new OrderItem();
            Integer productId = request.getProductId();
            Product product = productService.findById(productId);
            if (product == null) {
                throw new RuntimeException("Product not found with ID: " + productId);
            }
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setPrice(product.getPrice());
            orderItem.setQuantity(request.getQuantity());
            orderItem.setProductName(product.getProductName());
            if (product.getImageUrls() != null && !product.getImageUrls().isEmpty()) {
                orderItem.setProductImage(product.getImageUrls().get(0));
            } else {
                orderItem.setProductImage("https://res.cloudinary.com/dzljcagp9/image/upload/v1756805790/default_product_image_fdywaa.png");
            }
            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
            orderItems.add(orderItem);

            productService.updateProductStock(product, request.getQuantity());
        }
        order.setTotalAmount(totalAmount);
        order.setOrderItems(orderItems);
        if (paymentMethod == PaymentMethod.COD){
            order.setPaymentStatus(null);
            order.setPaymentRef(null);
            order.setStatus(OrderStatus.PENDING);
        } else {
            order.setPaymentStatus(PaymentStatus.UNPAID);
            final String uuid = UUID.randomUUID().toString().replace("-", "");
            order.setPaymentRef(uuid);
            order.setStatus(OrderStatus.UNPAID);
        }

        return orderRepository.save(order);
    }

    public boolean checkProductQuantity(ProductOrderRequest request){
        Integer productId = request.getProductId();
        Product product = productService.findById(productId);
        if (product == null) {
            throw new RuntimeException("Product not found with ID: " + productId);
        }
        Integer requestedQuantity = request.getQuantity();
        if(product.getStockQuantity() < requestedQuantity) {
            return false; // Not enough stock
        }
        return true; // Enough stock
    }

    public List<Order> findByUser(User user) {
        return orderRepository.findByUser(user);
    }

    public Order findById(Integer orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
    }

    public Order updateOrderStatus(Integer orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
        order.setStatus(status);
        return orderRepository.save(order);
    }

    public Order cancelOrder(Order order, String reason) {
     updateOrderStatus(order.getId(), OrderStatus.CANCELED);
     order.setPaymentRef(null);
     order.setPaymentStatus(null);
     order.setReasonCancel(reason);
     order.setCancelledAt(LocalDateTime.now());
     return orderRepository.save(order);
    }

    public Order findByPaymentRef(String paymentRef){
        return orderRepository.findByPaymentRef(paymentRef);
    }
}
