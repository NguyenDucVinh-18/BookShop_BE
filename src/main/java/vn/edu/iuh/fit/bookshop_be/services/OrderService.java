package vn.edu.iuh.fit.bookshop_be.services;

import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.bookshop_be.dtos.ProductOrderRequest;
import vn.edu.iuh.fit.bookshop_be.models.*;
import vn.edu.iuh.fit.bookshop_be.repositories.OrderRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductService productService;

    public OrderService(OrderRepository orderRepository, ProductService productService) {
        this.orderRepository = orderRepository;
        this.productService = productService;
    }

    public Order placeOrder(User user, PaymentMethod paymentMethod, Address address, String note, List<ProductOrderRequest> productOrderRequests) {
        Order order = new Order();
        order.setUser(user);
        order.setPaymentMethod(paymentMethod);
        order.setShippingAddress(address);
        order.setNote(note);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus("PENDING");
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
            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
            orderItems.add(orderItem);

            productService.updateProductStock(product, request.getQuantity());
        }
        order.setTotalAmount(totalAmount);
        order.setOrderItems(orderItems);

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
}
