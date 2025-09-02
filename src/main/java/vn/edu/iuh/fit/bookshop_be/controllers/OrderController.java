package vn.edu.iuh.fit.bookshop_be.controllers;

import com.cloudinary.utils.ObjectUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.iuh.fit.bookshop_be.dtos.PlaceOrderRequest;
import vn.edu.iuh.fit.bookshop_be.models.*;
import vn.edu.iuh.fit.bookshop_be.services.*;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/order")
public class OrderController{
    private final OrderService orderService;
    private final UserService userService;
    private final AddressService addressService;
    private final VNPayService vNPayService;

    public OrderController(OrderService orderService, UserService userService, AddressService addressService, VNPayService vNPayService) {
        this.orderService = orderService;
        this.userService = userService;
        this.addressService = addressService;
        this.vNPayService = vNPayService;
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
        try {
            User user = userService.getUserByToken(authHeader);

            if (user == null ) {
                response.put("status", "error");
                response.put("message", "Bạn cần đăng nhập để cập nhật ảnh đại diện");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            PaymentMethod paymentMethod = request.getPaymentMethod();
            if (paymentMethod == null) {
                response.put("status", "error");
                response.put("message", "Phương thức thanh toán không hợp lệ");
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

            if(request.getAddress() == null || request.getAddress().isEmpty()){
                response.put("status", "error");
                response.put("message", "Địa chỉ giao hàng không được để trống");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            if(request.getPhone() == null || request.getPhone().isEmpty() || !request.getPhone().matches("^(\\+84|0)\\d{9,10}$")){
                response.put("status", "error");
                response.put("message", "Số điện thoại không hợp lệ");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Thực hiện đặt hàng
            Map<String, Object> data = new HashMap<>();
            Order order = orderService.placeOrder(user, paymentMethod, request.getAddress(), request.getPhone() , request.getNote(), request.getProducts());
            if(order.getPaymentMethod() == PaymentMethod.BANKING){
                String vnpUrl = this.vNPayService.generateVNPayURL(order.getTotalAmount().doubleValue(), order.getPaymentRef());
                data.put("vnpUrl", vnpUrl);
            }
            response.put("status", "success");
            response.put("message", "Đặt hàng thành công");

            data.put("orderId", order.getId());
            data.put("totalAmount", order.getTotalAmount());
            data.put("orderStatus", order.getStatus());
            data.put("createdAt", order.getCreatedAt());
            data.put("shippingAddress", order.getAddress());
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

            if (user.getRole() == null || ( user.getRole() != Role.SALE && user.getRole() != Role.MANAGER)) {
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

            order = orderService.updateOrderStatus(orderId, OrderStatus.valueOf(status));
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
            @RequestBody Map<String, String> reason) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = userService.getUserByToken(authHeader);
            String reasonStr = reason.get("reason");
            if (reasonStr == null || reasonStr.isEmpty()) {
                response.put("status", "error");
                response.put("message", "Lý do hủy đơn hàng không được để trống");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
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

            if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.UNPAID) {
                response.put("status", "error");
                response.put("message", "Chỉ có thể hủy đơn hàng ở trạng thái PENDING hoặc UNPAID");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            order = orderService.cancelOrder(order, reasonStr);
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

    @PostMapping("/vnpay_payment")
    public String handlePlaceOrder(
//            @RequestHeader("Authorization") String authHeader,
//            @RequestParam("receiverName") String receiverName,
//            @RequestParam("receiverAddress") String receiverAddress,
//            @RequestParam("receiverPhone") String receiverPhone,
//            @RequestParam("paymentMethod") String paymentMethod,
            @RequestParam("totalPrice") String totalPrice) throws UnsupportedEncodingException {
//        User user = userService.getUserByToken(authHeader);

        final String uuid = UUID.randomUUID().toString().replace("-", "");

//        this.productService.handlePlaceOrder(currentUser, session,
//                receiverName, receiverAddress, receiverPhone,
//                paymentMethod, uuid);

//        if (!paymentMethod.equals("COD")) {
            String vnpUrl = this.vNPayService.generateVNPayURL(Double.parseDouble(totalPrice), uuid);

            return vnpUrl;
//        }

//        return "redirect:/thanks";

    }

    @GetMapping("/thanks")
    public String getThankYouPage(
            @RequestParam("vnp_ResponseCode") Optional<String> vnpayResponseCode,
            @RequestParam("vnp_TxnRef") Optional<String> paymentRef) {
        Order order = orderService.findByPaymentRef(paymentRef.orElse(""));
        if (order == null) {
            return null;
        }
        if (vnpayResponseCode.isPresent() && paymentRef.isPresent()) {
            // thanh toán qua VNPAY, cập nhật trạng thái order
            if(vnpayResponseCode.get().equals("00")) {
                order.setPaymentStatus(PaymentStatus.PAID);
                order.setStatus(OrderStatus.PENDING);
            } else {
                order.setPaymentStatus(PaymentStatus.FAILED);
            }
            orderService.save(order);
        }

        return order.getPaymentStatus().toString();
    }

    @PostMapping("/refund")
    public ResponseEntity<Map<String, Object>> refundOrder(
            @RequestParam String paymentRef,
            @RequestParam(defaultValue = "02") String transactionType) // 02: toàn bộ
    {

        Map<String, Object> response = new HashMap<>();
        try {
            Order order = orderService.findByPaymentRef(paymentRef);
            if (order == null) {
                response.put("status", "error");
                response.put("message", "Không tìm thấy đơn hàng với paymentRef = " + paymentRef);
                return ResponseEntity.badRequest().body(response);
            }

            if (order.getPaymentStatus() != PaymentStatus.PAID) {
                response.put("status", "error");
                response.put("message", "Đơn hàng chưa thanh toán, không thể hoàn tiền");
                return ResponseEntity.badRequest().body(response);
            }

            String transDate = order.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

            String result = vNPayService.refundVNPay(
                    order.getPaymentRef(),
                    order.getTotalAmount().longValue(),
                    transDate,
                    "adminRefund",
                    transactionType
            );

            order.setPaymentStatus(PaymentStatus.REFUNDED);
            order.setStatus(OrderStatus.CANCELED);
            order.setCancelledAt(LocalDateTime.now());
            orderService.save(order);

            response.put("status", "success");
            response.put("message", "Hoàn tiền thành công cho đơn hàng " + order.getId());
            response.put("vnpResponse", result);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Lỗi khi hoàn tiền: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


}
