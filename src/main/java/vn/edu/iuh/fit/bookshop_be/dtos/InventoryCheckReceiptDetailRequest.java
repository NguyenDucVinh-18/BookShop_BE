package vn.edu.iuh.fit.bookshop_be.dtos;

import lombok.Data;

@Data
public class InventoryCheckReceiptDetailRequest {
    private Integer productId;
    private int systemQuantity;
    private int actualQuantity;
    private String note;
}
