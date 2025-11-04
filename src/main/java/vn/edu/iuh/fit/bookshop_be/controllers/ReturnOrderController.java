package vn.edu.iuh.fit.bookshop_be.controllers;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.iuh.fit.bookshop_be.dtos.ReturnOrderRequest;
import vn.edu.iuh.fit.bookshop_be.models.*;
import vn.edu.iuh.fit.bookshop_be.services.CustomerService;
import vn.edu.iuh.fit.bookshop_be.services.EmployeeService;
import vn.edu.iuh.fit.bookshop_be.services.OrderService;
import vn.edu.iuh.fit.bookshop_be.services.ReturnOrderService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/return-order")
public class ReturnOrderController {
    private final ReturnOrderService returnOrderService;
    private final OrderService orderService;
    private final EmployeeService employeeService;
    private final CustomerService customerService;
    private final Cloudinary cloudinary;

    public ReturnOrderController(ReturnOrderService returnOrderService, OrderService orderService, EmployeeService employeeService, CustomerService customerService, Cloudinary cloudinary) {
        this.returnOrderService = returnOrderService;
        this.orderService = orderService;
        this.employeeService = employeeService;
        this.customerService = customerService;
        this.cloudinary = cloudinary;
    }

    /**
     * Tạo yêu cầu trả hàng cho đơn hàng đã mua
     *
     * @param authHeader  Header xác thực người dùng
     * @param request     Dữ liệu yêu cầu trả hàng
     * @param mediaFiles  Ảnh và video đính kèm (nếu có)
     * @return Phản hồi với trạng thái và thông tin yêu cầu trả hàng
     */
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> create(
            @RequestHeader("Authorization") String authHeader,
            @RequestPart("request") ReturnOrderRequest request,
            @RequestPart(value = "medias", required = false) MultipartFile[] mediaFiles
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            Customer customer = customerService.getCustomerByToken(authHeader);
            if (customer == null) {
                response.put("status", "error");
                response.put("message", "Bạn cần đăng nhập để viết đánh giá sản phẩm");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            Order order = orderService.findByIdAndUser(request.getOrderId(), customer);
            if (order == null) {
                response.put("status", "error");
                response.put("message", "Đơn hàng không tồn tại hoặc không thuộc về bạn");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            if(order.getStatus() != OrderStatus.DELIVERED){
                response.put("status", "error");
                response.put("message", "Chỉ có thể tạo yêu cầu trả hàng cho các đơn hàng đã giao thành công");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            if(order.getCreatedAt().isAfter(java.time.LocalDateTime.now().minusDays(3))) {
                response.put("status", "error");
                response.put("message", "Chỉ có thể tạo yêu cầu trả hàng cho các đơn hàng đã mua ít nhất 7 ngày trước");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            ReturnOrder returnOrder = returnOrderService.createReturnOrder(order, request.getReason(), request.getNote());

            if(returnOrder != null) {
                // Xử lý tải lên ảnh và video
                List<String> mediaUrls = new ArrayList<>();
                if (mediaFiles != null ) {
                    for (MultipartFile file : mediaFiles) {
                        if (file != null && !file.isEmpty()) {
                            // Kiểm tra định dạng tệp (chỉ cho phép ảnh và video)
                            String contentType = file.getContentType();
                            if (contentType != null && (contentType.startsWith("image/") || contentType.startsWith("video/"))) {
                                try {
                                    String folderName = "return_order/" + order.getId() + "-" + order.getOrderCode();
                                    Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                                            ObjectUtils.asMap(
                                                    "folder", folderName,
                                                    "resource_type", contentType.startsWith("video/") ? "video" : "image"
                                            ));
                                    String mediaUrl = (String) uploadResult.get("secure_url");
                                    mediaUrls.add(mediaUrl);
                                } catch (IOException e) {
                                    response.put("status", "error");
                                    response.put("message", "Lỗi khi tải tệp lên Cloudinary: " + e.getMessage());
                                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                                }
                            } else {
                                response.put("status", "error");
                                response.put("message", "Định dạng tệp không hợp lệ. Chỉ chấp nhận ảnh (PNG, JPG) hoặc video (MP4).");
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                            }
                        }
                    }
                }
                returnOrder.setMediaUrls(mediaUrls);
                returnOrder = returnOrderService.save(returnOrder);

                order.setStatus(OrderStatus.REFUND_REQUESTED);
                orderService.save(order);
            }

            response.put("status", "success");
            response.put("message", "Tạo yêu cầu trả hàng thành công");
            Map<String, Object> data = new HashMap<>();
            data.put("returnOrder", returnOrder);
            response.put("data", data);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Đã xảy ra lỗi khi tạo yêu cầu trả hàng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/getAll")
    public ResponseEntity<Map<String, Object>> getAllReturnOrders(
            @RequestHeader("Authorization") String authHeader
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            Employee employee = employeeService.getEmployeeByToken(authHeader);
            if (employee == null) {
                response.put("status", "error");
                response.put("message", "Bạn cần đăng nhập để thực hiện hành động này");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            if (employee.getRole() == null || ( employee.getRole() != Role.STAFF && employee.getRole() != Role.MANAGER)) {
                response.put("status", "error");
                response.put("message", "Bạn không có quyền cập nhật trạng thái đơn hàng");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            List<ReturnOrder> returnOrders = returnOrderService.getAll();

            response.put("status", "success");
            response.put("message", "Lấy danh sách yêu cầu trả hàng thành công");
            Map<String, Object> data = new HashMap<>();
            data.put("returnOrders", returnOrders);
            response.put("data", data);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Đã xảy ra lỗi khi lấy danh sách yêu cầu trả hàng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/update-status/{returnOrderId}")
    public ResponseEntity<Map<String, Object>> updateReturnOrderStatus(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("returnOrderId") Integer returnOrderId,
            @RequestParam("status") ReturnOrderStatus status
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            Employee employee = employeeService.getEmployeeByToken(authHeader);
            if (employee == null) {
                response.put("status", "error");
                response.put("message", "Bạn cần đăng nhập để thực hiện hành động này");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            if (employee.getRole() == null || (employee.getRole() != Role.STAFF && employee.getRole() != Role.MANAGER)) {
                response.put("status", "error");
                response.put("message", "Bạn không có quyền cập nhật trạng thái đơn hàng");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            ReturnOrder existingReturnOrder = returnOrderService.getById(returnOrderId);
            if (existingReturnOrder == null) {
                response.put("status", "error");
                response.put("message", "Yêu cầu trả hàng không tồn tại");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            ReturnOrder returnOrder = returnOrderService.save(returnOrderService.updateStatus(employee, existingReturnOrder , status));
            response.put("status", "success");
            response.put("message", "Cập nhật trạng thái yêu cầu trả hàng thành công");
            Map<String, Object> data = new HashMap<>();
            data.put("returnOrder", returnOrder);
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Đã xảy ra lỗi khi cập nhật trạng thái yêu cầu trả hàng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
