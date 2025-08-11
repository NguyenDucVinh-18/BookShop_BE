package vn.edu.iuh.fit.bookshop_be.models;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "cart_items")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private Integer quantity;

    @Column(precision = 10, scale = 2)
    private BigDecimal price; // giá tại thời điểm thêm

    public CartItem() {}

    public CartItem( Product product, Integer quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }


    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
