package vn.edu.iuh.fit.bookshop_be.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "statistics")
public class Statistic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDate date;

    @Column(name = "total_orders")
    private Integer totalOrders;

    @Column(name = "total_revenue", precision = 13, scale = 2)
    private BigDecimal totalRevenue;

    public Statistic() {}

    public Statistic(LocalDate date, Integer totalOrders, BigDecimal totalRevenue) {
        this.date = date;
        this.totalOrders = totalOrders;
        this.totalRevenue = totalRevenue;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Integer getTotalOrders() { return totalOrders; }
    public void setTotalOrders(Integer totalOrders) { this.totalOrders = totalOrders; }

    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
}
