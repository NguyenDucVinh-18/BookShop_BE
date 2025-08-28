package vn.edu.iuh.fit.bookshop_be.dtos;

import lombok.Data;
import vn.edu.iuh.fit.bookshop_be.models.PaymentMethod;

import java.util.List;

@Data
public class PlaceOrderRequest {
    private List<ProductOrderRequest> products;
    private PaymentMethod paymentMethod;
    private Integer shippingAddressId;
    private String note;
}
