package vn.edu.iuh.fit.bookshop_be.controllers;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.iuh.fit.bookshop_be.dtos.PlaceOrderRequest;
import vn.edu.iuh.fit.bookshop_be.models.Address;
import vn.edu.iuh.fit.bookshop_be.models.Order;
import vn.edu.iuh.fit.bookshop_be.models.PaymentMethod;
import vn.edu.iuh.fit.bookshop_be.models.User;
import vn.edu.iuh.fit.bookshop_be.services.AddressService;
import vn.edu.iuh.fit.bookshop_be.services.OrderService;
import vn.edu.iuh.fit.bookshop_be.services.PaymentMethodService;
import vn.edu.iuh.fit.bookshop_be.services.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/order")
public class OrderController{
    private final OrderService orderService;
    private final UserService userService;
    private final PaymentMethodService paymentMethodService;
    private final AddressService addressService;

    public OrderController(OrderService orderService, UserService userService, PaymentMethodService paymentMethodService, AddressService addressService) {
        this.orderService = orderService;
        this.userService = userService;
        this.paymentMethodService = paymentMethodService;
        this.addressService = addressService;
    }

    /**
     * Đặt hàng
     * @param authHeader
     * @param request
     * @return trả về thông tin đơn hàng đã đặt
     */
    @PostMapping("/placeOrder")
    public ResponseEntity<Map<String, Object>> updateAvatar(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody PlaceOrderRequest request
            ) {
        Map<String, Object> response = new HashMap<>();
        System.out.println("Received request to place order: " + request);
        try {
            User user = userService.getUserByToken(authHeader);

            if (user == null ) {
                response.put("status", "error");
                response.put("message", "Bạn cần đăng nhập để cập nhật ảnh đại diện");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            PaymentMethod paymentMethod = paymentMethodService.findById(request.getPaymentMethodId());
            Address address = addressService.findById(request.getShippingAddressId());
            if (paymentMethod == null) {
                response.put("status", "error");
                response.put("message", "Phương thức thanh toán không hợp lệ");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            if (address == null) {
                response.put("status", "error");
                response.put("message", "Địa chỉ giao hàng không hợp lệ");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Kiểm tra số lượng sản phẩm
            for (var productOrderRequest : request.getProducts()) {
                if (!orderService.checkProductQuantity(productOrderRequest)) {
                    response.put("status", "error");
                    response.put("message", "Số lượng sản phẩm không đủ: " + productOrderRequest.getProductId());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }
            }
            // Thực hiện đặt hàng
            Order order = orderService.placeOrder(user, paymentMethod, address, request.getNote(), request.getProducts());
            response.put("status", "success");
            response.put("message", "Đặt hàng thành công");
            Map<String, Object> data = new HashMap<>();
            data.put("orderId", order.getId());
            data.put("totalAmount", order.getTotalAmount());
            data.put("orderStatus", order.getStatus());
            data.put("createdAt", order.getCreatedAt());
            data.put("shippingAddress", order.getShippingAddress());
            data.put("paymentMethod", order.getPaymentMethod());
            data.put("note", order.getNote());
            data.put("user", user.getId());
            data.put("orderItems", order.getOrderItems());
            response.put("data", data);


            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Lỗi khi đặt hàng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Lấy danh sách đơn hàng của người dùng
     * @param authHeader
     * @return trả về danh sách đơn hàng của người dùng
     */
    @GetMapping("/getOrders")
    public ResponseEntity<Map<String, Object>> getOrders(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = userService.getUserByToken(authHeader);
            if (user == null) {
                response.put("status", "error");
                response.put("message", "Bạn cần đăng nhập để xem đơn hàng");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            List<Order> orders = orderService.findByUser(user);
            response.put("status", "success");
            response.put("message", "Lấy danh sách đơn hàng thành công");
            response.put("data", orders);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Lỗi khi lấy danh sách đơn hàng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Cập nhật trạng thái đơn hàng
     * @param authHeader
     * @param orderId
     * @param status
     * @return trả về thông tin đơn hàng đã cập nhật
     */
    @PutMapping("/updateOrderStatus/{orderId}")
    public ResponseEntity<Map<String, Object>> updateOrderStatus(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer orderId,
            @RequestParam String status)
    {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = userService.getUserByToken(authHeader);
            if (user == null) {
                response.put("status", "error");
                response.put("message", "Bạn cần đăng nhập để cập nhật trạng thái đơn hàng");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            if (user.getRole() == null || !user.getRole().equals("ADMIN")) {
                response.put("status", "error");
                response.put("message", "Bạn không có quyền cập nhật trạng thái đơn hàng");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            Order order = orderService.findById(orderId);
            if (order == null) {
                response.put("status", "error");
                response.put("message", "Đơn hàng không tồn tại");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            order = orderService.updateOrderStatus(orderId, status);
            response.put("status", "success");
            response.put("message", "Cập nhật trạng thái đơn hàng thành công");
            response.put("data", order);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Lỗi khi cập nhật trạng thái đơn hàng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Hủy đơn hàng
     * @param authHeader
     * @param orderId
     * @param reason
     * @return trả về thông tin đơn hàng đã hủy
     */
    @PutMapping("/cancelOrder/{orderId}")
    public ResponseEntity<Map<String, Object>> cancelOrder(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer orderId,
            @RequestBody String reason) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = userService.getUserByToken(authHeader);
            if (user == null) {
                response.put("status", "error");
                response.put("message", "Bạn cần đăng nhập để hủy đơn hàng");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            Order order = orderService.findById(orderId);
            if (order == null) {
                response.put("status", "error");
                response.put("message", "Đơn hàng không tồn tại");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            if (!order.getUser().getId().equals(user.getId())) {
                response.put("status", "error");
                response.put("message", "Bạn không có quyền hủy đơn hàng này");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            if(!order.getStatus().equalsIgnoreCase("PENDING")){
                response.put("status", "error");
                response.put("message", "Chỉ có thể hủy đơn hàng đang chờ xử lý");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            order = orderService.cancelOrder(order, reason);
            response.put("status", "success");
            response.put("message", "Hủy đơn hàng thành công");
            response.put("data", order);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Lỗi khi hủy đơn hàng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


}
