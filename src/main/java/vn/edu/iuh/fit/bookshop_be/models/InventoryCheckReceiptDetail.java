package vn.edu.iuh.fit.bookshop_be.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "inventory_check_receipt_detail")
public class InventoryCheckReceiptDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_check_receipt_id", nullable = false)
    @JsonBackReference
    private InventoryCheckReceipt inventoryCheckReceipt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "system_quantity",nullable = false)
    private int systemQuantity;

    @Column(name = "actual_quantity", nullable = false)
    private int actualQuantity;

    private String note;

    public InventoryCheckReceiptDetail() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public InventoryCheckReceipt getInventoryCheckReceipt() {
        return inventoryCheckReceipt;
    }

    public void setInventoryCheckReceipt(InventoryCheckReceipt inventoryCheckReceipt) {
        this.inventoryCheckReceipt = inventoryCheckReceipt;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getSystemQuantity() {
        return systemQuantity;
    }

    public void setSystemQuantity(int systemQuantity) {
        this.systemQuantity = systemQuantity;
    }

    public int getActualQuantity() {
        return actualQuantity;
    }

    public void setActualQuantity(int actualQuantity) {
        this.actualQuantity = actualQuantity;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
