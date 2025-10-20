package vn.edu.iuh.fit.bookshop_be.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.iuh.fit.bookshop_be.models.Employee;
import vn.edu.iuh.fit.bookshop_be.models.Role;
import vn.edu.iuh.fit.bookshop_be.services.CustomerService;
import vn.edu.iuh.fit.bookshop_be.services.EmployeeService;
import vn.edu.iuh.fit.bookshop_be.services.OrderService;
import vn.edu.iuh.fit.bookshop_be.services.ProductService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/statistic")
public class StatisticalController {
    private final OrderService orderService;
    private final ProductService productService;
    private final EmployeeService employeeService;
    private final CustomerService customerService;

    public StatisticalController(OrderService orderService, ProductService productService, EmployeeService employeeService, CustomerService customerService) {
        this.orderService = orderService;
        this.productService = productService;
        this.employeeService = employeeService;
        this.customerService = customerService;
    }

    /**
     * Lấy tổng doanh thu, tổng đơn hàng, tổng sản phẩm, tổng nhân viên, tổng khách hàng
     * @param authHeader
     * @return
     */
    @GetMapping("/total")
    public ResponseEntity<Map<String, Object>> getTotalRevenue(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();
        try {
            Employee employee = employeeService.getEmployeeByToken(authHeader);
            if (employee == null) {
                response.put("status", "error");
                response.put("message", "Bạn cần đăng nhập để thực hiện hành động này");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Kiểm tra quyền của người dùng
            if (employee.getRole() == null || employee.getRole() != Role.MANAGER) {
                response.put("status", "error");
                response.put("message", "Bạn không có quyền thực hiện hành động này");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            Long totalOrders = orderService.countOrders();
            Double totalRevenue = orderService.calculateTotalRevenue();
            Long totalProducts = productService.countProducts();
            Long totalEmployees = employeeService.countEmployees();
            Long totalCustomers = customerService.countCustomers();

            Map<String, Object> data = new HashMap<>();

            data.put("totalOrders", totalOrders);
            data.put("totalRevenue", totalRevenue);
            data.put("totalProducts", totalProducts);
            data.put("totalEmployees", totalEmployees);
            data.put("totalCustomers", totalCustomers);
            response.put("data", data);
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "An error occurred while fetching total revenue.");
            return ResponseEntity.status(500).body(response);
        }

    }

    /**
     * Lấy tổng số sản phẩm đã bán được cho mỗi sản phẩm
     * @param authHeader
     * @return
     */
    @GetMapping("/products-sold")
    public ResponseEntity<Map<String, Object>> getTotalProductsSold(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();
        try {
            Employee employee = employeeService.getEmployeeByToken(authHeader);
            if (employee == null) {
                response.put("status", "error");
                response.put("message", "Bạn cần đăng nhập để thực hiện hành động này");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            // Kiểm tra quyền của người dùng
            if (employee.getRole() == null || employee.getRole() != Role.MANAGER) {
                response.put("status", "error");
                response.put("message", "Bạn không có quyền thực hiện hành động này");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            Map<String, Object> data = new HashMap<>();
            for (var product : productService.getAllProducts()) {
                data.put(product.getProductName(), orderService.countTotalProductSold(product.getId()));
            }

            response.put("data", data);
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "An error occurred while fetching total products sold.");
            return ResponseEntity.status(500).body(response);
        }
    }


}
