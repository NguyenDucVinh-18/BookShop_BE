package vn.edu.iuh.fit.bookshop_be.dtos;

import lombok.Data;
import vn.edu.iuh.fit.bookshop_be.models.TypeStockReceipt;

import java.util.List;

@Data
public class InventoryCheckReceiptRequest {
    private List<InventoryCheckReceiptDetailRequest> products;
    private String nameInventoryCheckReceipt;
    private String note;
}
