package vn.edu.iuh.fit.bookshop_be.services;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.bookshop_be.dtos.InventoryCheckReceiptDetailRequest;
import vn.edu.iuh.fit.bookshop_be.dtos.ProductStockReceiptRequest;
import vn.edu.iuh.fit.bookshop_be.models.*;
import vn.edu.iuh.fit.bookshop_be.repositories.InventoryCheckReceiptDetailRepository;
import vn.edu.iuh.fit.bookshop_be.repositories.InventoryCheckReceiptRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class InventoryCheckReceiptService {
    private final InventoryCheckReceiptRepository inventoryCheckReceiptRepository;
    private final InventoryCheckReceiptDetailRepository inventoryCheckReceiptDetailRepository;

    public InventoryCheckReceiptService(InventoryCheckReceiptRepository inventoryCheckReceiptRepository, InventoryCheckReceiptDetailRepository inventoryCheckReceiptDetailRepository) {
        this.inventoryCheckReceiptRepository = inventoryCheckReceiptRepository;
        this.inventoryCheckReceiptDetailRepository = inventoryCheckReceiptDetailRepository;
    }

    public InventoryCheckReceipt save(String name, Employee employee, String note , List<InventoryCheckReceiptDetailRequest> inventoryCheckReceiptDetailRequests) {
        InventoryCheckReceipt inventoryCheckReceipt = new InventoryCheckReceipt();
        inventoryCheckReceipt.setEmployee(employee);
        inventoryCheckReceipt.setNote(note);
        inventoryCheckReceipt.setNameInventoryCheckReceipt(name);
        List<InventoryCheckReceiptDetail> details = new ArrayList<>();
        for(InventoryCheckReceiptDetailRequest request : inventoryCheckReceiptDetailRequests) {
            InventoryCheckReceiptDetail detail = new InventoryCheckReceiptDetail();
            detail.setInventoryCheckReceipt(inventoryCheckReceipt);
            Product product = new Product();
            product.setId(request.getProductId());
            detail.setProduct(product);
            detail.setSystemQuantity(request.getSystemQuantity());
            detail.setActualQuantity(request.getActualQuantity());
            detail.setNote(request.getNote());
            details.add(detail);
        }
        inventoryCheckReceipt.setDetails(details);
        InventoryCheckReceipt savedReceipt = inventoryCheckReceiptRepository.save(inventoryCheckReceipt);
        for(InventoryCheckReceiptDetail detail : details) {
            inventoryCheckReceiptDetailRepository.save(detail);
        }
        return savedReceipt;
    }

    public List<InventoryCheckReceipt> findAll() {
        return inventoryCheckReceiptRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    public List<InventoryCheckReceipt> getInventoryChecksDateBetween(
            LocalDate startDate,
            LocalDate endDate
    ) {
        LocalDateTime startDateTime = (startDate != null)
                ? startDate.atStartOfDay()
                : LocalDate.now().withDayOfMonth(1).atStartOfDay();

        LocalDateTime endDateTime = (endDate != null)
                ? endDate.atTime(23, 59, 59)
                : LocalDate.now().atTime(23, 59, 59);
        return inventoryCheckReceiptRepository.getInventoryChecksDateBetween(startDateTime, endDateTime);
    }
}
