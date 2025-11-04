package vn.edu.iuh.fit.bookshop_be.dtos;

import lombok.Data;

@Data
public class ReturnOrderRequest {
    private Integer orderId;
    private String reason;
    private String note;
}
