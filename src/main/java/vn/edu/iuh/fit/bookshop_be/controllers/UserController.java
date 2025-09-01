package vn.edu.iuh.fit.bookshop_be.controllers;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.iuh.fit.bookshop_be.dtos.AddressRequest;
import vn.edu.iuh.fit.bookshop_be.dtos.UpdateInfoRequest;
import vn.edu.iuh.fit.bookshop_be.models.Address;
import vn.edu.iuh.fit.bookshop_be.models.User;
import vn.edu.iuh.fit.bookshop_be.security.JwtUtil;
import vn.edu.iuh.fit.bookshop_be.services.AddressService;
import vn.edu.iuh.fit.bookshop_be.services.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final Cloudinary cloudinary;
    private final AddressService addressService;

    public UserController(UserService userService, JwtUtil jwtUtil, Cloudinary cloudinary, AddressService addressService) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.cloudinary = cloudinary;
        this.addressService = addressService;
    }

    /**
     * Cập nhật ảnh đại diện cho người dùng
     * @param authHeader
     * @param image
     * @return trả về thông tin người dùng sau khi cập nhật ảnh đại diện
     */
    @PostMapping("/updateAvatar")
    public ResponseEntity<Map<String, Object>> updateAvatar(
            @RequestHeader("Authorization") String authHeader,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = userService.getUserByToken(authHeader);

            if (user == null ) {
                response.put("status", "error");
                response.put("message", "Bạn cần đăng nhập để cập nhật ảnh đại diện");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            // Kiểm tra có gửi ảnh không
            if (image == null || image.isEmpty()) {
                response.put("status", "error");
                response.put("message", "Vui lòng chọn ảnh để cập nhật");
                return ResponseEntity.badRequest().body(response);
            }

            String folderName =  "avatars/" + user.getEmail() ;
            // Upload ảnh lên Cloudinary
            Map uploadResult = cloudinary.uploader().upload(image.getBytes(),
                    ObjectUtils.asMap("folder", folderName));

            String imageUrl = (String) uploadResult.get("secure_url");

            // Cập nhật avatar vào user
            user.setAvatarUrl(imageUrl);
            userService.save(user);

            // Trả kết quả
            response.put("status", "success");
            response.put("message", "Cập nhật ảnh đại diện thành công");
            User userRender = new User();
            userRender.setId(user.getId());
            userRender.setUsername(user.getUsername());
            userRender.setEmail(user.getEmail());
            userRender.setRole(user.getRole());
            userRender.setAvatarUrl(user.getAvatarUrl());

            Map<String, Object> data = new HashMap<>();
            data.put("user", userRender);
            response.put("data", data);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Lỗi khi cập nhật ảnh: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }

    /*** Cập nhật thông tin người dùng
     * @param authHeader
     * @param request
     * @return trả về thông tin người dùng sau khi cập nhật
     */
    @PutMapping("/updateInfo")
    public ResponseEntity<Map<String, Object>> updateInfo(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UpdateInfoRequest request
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = userService.getUserByToken(authHeader);
            if (user == null) {
                response.put("status", "error");
                response.put("message", "Bạn cần đăng nhập để cập nhật thông tin");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            if( request.getUsername() == null || request.getUsername().isEmpty() || request.getPhone() == null || request.getPhone().isEmpty()) {
                response.put("status", "error");
                response.put("message", "Nhập đầy đủ thông tin để cập nhật");
                return ResponseEntity.badRequest().body(response);
            }

            if( !request.getPhone().matches("^0[0-9]{9,10}$")) {
                response.put("status", "error");
                response.put("message", "Số điện thoại phải bắt đầu bằng số 0 và có độ dài từ 10 đến 11 chữ số");
                return ResponseEntity.badRequest().body(response);
            }

            // Cập nhật thông tin người dùng
            user.setUsername(request.getUsername());
            user.setPhone(request.getPhone());
            userService.save(user);

            response.put("status", "success");
            response.put("message", "Cập nhật thông tin thành công");
            User userRender = new User();
            userRender.setId(user.getId());
            userRender.setUsername(user.getUsername());
            userRender.setEmail(user.getEmail());
            userRender.setRole(user.getRole());
            userRender.setAvatarUrl(user.getAvatarUrl());
            userRender.setPhone(user.getPhone());

            Map<String, Object> data = new HashMap<>();
            data.put("user", userRender);
            response.put("data", data);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Lỗi khi cập nhật thông tin: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Thêm địa chỉ mới cho người dùng
     * @param request
     * @param authHeader
     * @return danh sách địa chỉ của người dùng sau khi thêm
     */
    @PostMapping("/addAddress")
    public ResponseEntity<Map<String, Object>> addAddress(@RequestBody AddressRequest request, @RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = userService.getUserByToken(authHeader);
            if (user == null) {
                response.put("status", "error");
                response.put("message", "Bạn cần đăng nhập để thêm địa chỉ");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            Address address = new Address();
            address.setStreet(request.getStreet());
            address.setWard(request.getWard());
            address.setDistrict(request.getDistrict());
            address.setCity(request.getCity());
            address.setNote(request.getNote());
            address.setUser(user);
            Address savedAddress = addressService.save(address);

            response.put("status", "success");
            response.put("message", "Thêm địa chỉ thành công");
            Map<String, Object> data = new HashMap<>();
            User userRender = new User();
            userRender.setId(user.getId());
            userRender.setUsername(user.getUsername());
            userRender.setEmail(user.getEmail());
            userRender.setRole(user.getRole());
            userRender.setAvatarUrl(user.getAvatarUrl());
            List<Address> addresses = user.getAddresses();
            addresses.add(savedAddress);
            userRender.setAddresses(addresses);
            data.put("user", userRender);
            response.put("data", data);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Lỗi khi thêm địa chỉ: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Xóa địa chỉ của người dùng
     * @param authHeader
     * @return danh sách địa chỉ của người dùng sau khi xóa
     */
    @DeleteMapping("/deleteAddress/{addressId}")
    public ResponseEntity<Map<String, Object>> deleteAddress(@PathVariable("addressId") Integer id, @RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = userService.getUserByToken(authHeader);
            if (user == null) {
                response.put("status", "error");
                response.put("message", "Bạn cần đăng nhập để xóa địa chỉ");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            Address address = addressService.findById(id);
            if (address == null) {
                response.put("status", "error");
                response.put("message", "Địa chỉ không tồn tại");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            if (!address.getUser().getId().equals(user.getId())) {
                response.put("status", "error");
                response.put("message", "Bạn không có quyền xóa địa chỉ này");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            addressService.deleteById(id);
            response.put("status", "success");
            response.put("message", "Xóa địa chỉ thành công");
            User userRender = new User();
            userRender.setId(user.getId());
            userRender.setUsername(user.getUsername());
            userRender.setEmail(user.getEmail());
            userRender.setRole(user.getRole());
            userRender.setAvatarUrl(user.getAvatarUrl());
            List<Address> addresses = user.getAddresses();
            addresses.removeIf(addr -> addr != null && addr.getId() == id);
            userRender.setAddresses(addresses);
            Map<String, Object> data = new HashMap<>();
            data.put("user", userRender);
            response.put("data", data);


            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Lỗi khi xóa địa chỉ: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }

    /**
     * Cập nhật địa chỉ của người dùng
     * @param id
     * @param authHeader
     * @param request
     * @return danh sách địa chỉ của người dùng sau khi cập nhật
     */
    @PutMapping("/updateAddress/{addressId}")
    public ResponseEntity<Map<String, Object>> updateAddress(
            @PathVariable("addressId") Integer id,
            @RequestHeader("Authorization") String authHeader,
            @RequestBody AddressRequest request)
    {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = userService.getUserByToken(authHeader);
            if (user == null) {
                response.put("status", "error");
                response.put("message", "Bạn cần đăng nhập để thêm địa chỉ");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            Address existingAddress = addressService.findByIdAndUser(id, user);
            if (existingAddress == null) {
                response.put("status", "error");
                response.put("message", "Địa chỉ không tồn tại hoặc bạn không có quyền sửa địa chỉ này");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Address address = addressService.findById(id);
            address.setStreet(request.getStreet());
            address.setWard(request.getWard());
            address.setDistrict(request.getDistrict());
            address.setCity(request.getCity());
            address.setNote(request.getNote());
            address.setUser(user);
            addressService.save(address);

            response.put("status", "success");
            response.put("message", "Thêm cập nhật thành công");
            Map<String, Object> data = new HashMap<>();
            User userRender = new User();
            userRender.setId(user.getId());
            userRender.setUsername(user.getUsername());
            userRender.setEmail(user.getEmail());
            userRender.setRole(user.getRole());
            userRender.setAvatarUrl(user.getAvatarUrl());
            List<Address> addresses = user.getAddresses();
            addresses.add(address);
            userRender.setAddresses(addresses);
            data.put("user", userRender);
            response.put("data", data);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Lỗi khi cập nhật địa chỉ: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

/**
     * Lấy danh sách địa chỉ của người dùng
     * @param authHeader
     * @return danh sách địa chỉ của người dùng
     */
    @GetMapping("/addresses")
    public ResponseEntity<Map<String, Object>> getAddresses(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = userService.getUserByToken(authHeader);
            if (user == null) {
                response.put("status", "error");
                response.put("message", "Bạn cần đăng nhập để xem địa chỉ");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            List<Address> addresses = addressService.findByUser(user);
            response.put("status", "success");
            response.put("message", "Lấy danh sách địa chỉ thành công");
            Map<String, Object> data = new HashMap<>();
            data.put("addresses", addresses);
            response.put("data", data);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Lỗi khi lấy danh sách địa chỉ: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
