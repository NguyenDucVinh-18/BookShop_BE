package vn.edu.iuh.fit.bookshop_be.controllers;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.fit.bookshop_be.dtos.InventoryCheckReceiptRequest;
import vn.edu.iuh.fit.bookshop_be.dtos.StockReceiptRequest;
import vn.edu.iuh.fit.bookshop_be.models.Employee;
import vn.edu.iuh.fit.bookshop_be.models.InventoryCheckReceipt;
import vn.edu.iuh.fit.bookshop_be.models.Role;
import vn.edu.iuh.fit.bookshop_be.services.EmployeeService;
import vn.edu.iuh.fit.bookshop_be.services.InventoryCheckReceiptService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory-check-receipt")
public class InventoryCheckReceiptController {
    private final InventoryCheckReceiptService inventoryCheckReceiptService;
    private final EmployeeService employeeService;


    public InventoryCheckReceiptController(InventoryCheckReceiptService inventoryCheckReceiptService, EmployeeService employeeService) {
        this.inventoryCheckReceiptService = inventoryCheckReceiptService;
        this.employeeService = employeeService;
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> create(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody InventoryCheckReceiptRequest request
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            Employee employee = employeeService.getEmployeeByToken(authHeader);
            if( employee == null ) {
                response.put("status", "error");
                response.put("message", "Bạn cần đăng nhập để thực hiện hành động này");
                return ResponseEntity.status(403).body(response);
            }
            if(employee.getRole() != Role.STAFF && employee.getRole() != Role.MANAGER) {
                response.put("status", "error");
                response.put("message", "Bạn không có quyền thực hiện hành động này");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            // Thực hiện tạo phiếu kiểm kho
            Map<String, Object> data = new HashMap<>();
            InventoryCheckReceipt inventoryCheckReceipt = inventoryCheckReceiptService.save(request.getNameInventoryCheckReceipt(), employee, request.getNote(), request.getProducts());
            response.put("status", "success");
            response.put("message", "Tạo phiếu kiểm kho thành công");
            data.put("InventoryCheckReceiptID", inventoryCheckReceipt.getId());
            data.put("nameInventoryCheckReceipt", inventoryCheckReceipt.getNameInventoryCheckReceipt());
            data.put("createdAt", inventoryCheckReceipt.getCreatedAt());
            data.put("note", inventoryCheckReceipt.getNote());
            data.put("employee", inventoryCheckReceipt.getEmployee());
            data.put("details", inventoryCheckReceipt.getDetails());
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Đã có lỗi xảy ra: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/getAll")
    public ResponseEntity<Map<String, Object>> getAll(
            @RequestHeader("Authorization") String authHeader
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            Employee employee = employeeService.getEmployeeByToken(authHeader);
            if (employee == null) {
                response.put("status", "error");
                response.put("message", "Bạn cần đăng nhập để thực hiện hành động này");
                return ResponseEntity.status(403).body(response);
            }
            if (employee.getRole() != Role.STAFF && employee.getRole() != Role.MANAGER) {
                response.put("status", "error");
                response.put("message", "Bạn không có quyền thực hiện hành động này");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            // Lấy tất cả phiếu kiểm kho
            Map<String, Object> data = new HashMap<>();
            data.put("inventoryCheckReceipts", inventoryCheckReceiptService.findAll());
            response.put("status", "success");
            response.put("message", "Lấy danh sách phiếu kiểm kho thành công");
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Đã có lỗi xảy ra: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/getDateBetween")
    public ResponseEntity<Map<String, Object>> getDateBetween(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            Employee employee = employeeService.getEmployeeByToken(authHeader);
            if (employee == null) {
                response.put("status", "error");
                response.put("message", "Bạn cần đăng nhập để thực hiện hành động này");
                return ResponseEntity.status(403).body(response);
            }
            if (employee.getRole() != Role.STAFF && employee.getRole() != Role.MANAGER) {
                response.put("status", "error");
                response.put("message", "Bạn không có quyền thực hiện hành động này");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            // Lấy phiếu kiểm kho theo khoảng thời gian
            Map<String, Object> data = new HashMap<>();
            data.put("inventoryCheckReceipts", inventoryCheckReceiptService.getInventoryChecksDateBetween(startDate, endDate));
            response.put("status", "success");
            response.put("message", "Lấy danh sách phiếu kiểm kho thành công");
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Đã có lỗi xảy ra: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
