package vn.edu.iuh.fit.bookshop_be.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "inventory_check_receipt")
public class InventoryCheckReceipt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = true)
    @JsonIgnore
    private Employee employee;

    @Column(name = "name_inventory_check_receipt")
    private String nameInventoryCheckReceipt;

    private String note;

    private LocalDateTime createdAt = LocalDateTime.now();


    @OneToMany(mappedBy = "inventoryCheckReceipt", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<InventoryCheckReceiptDetail> details = new ArrayList<>();

    public InventoryCheckReceipt() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public String getNameInventoryCheckReceipt() {
        return nameInventoryCheckReceipt;
    }

    public void setNameInventoryCheckReceipt(String nameInventoryCheckReceipt) {
        this.nameInventoryCheckReceipt = nameInventoryCheckReceipt;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<InventoryCheckReceiptDetail> getDetails() {
        return details;
    }

    public void setDetails(List<InventoryCheckReceiptDetail> details) {
        this.details = details;
    }
}
