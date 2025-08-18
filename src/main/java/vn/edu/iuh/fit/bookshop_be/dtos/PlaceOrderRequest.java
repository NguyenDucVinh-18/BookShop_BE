package vn.edu.iuh.fit.bookshop_be.dtos;

import lombok.Data;

import java.util.List;

@Data
public class PlaceOrderRequest {
    private List<ProductOrderRequest> products;
    private Integer paymentMethodId;
    private Integer shippingAddressId;
    private String note;
}
