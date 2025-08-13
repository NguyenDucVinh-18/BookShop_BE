package vn.edu.iuh.fit.bookshop_be.dtos;

import lombok.Data;

@Data
public class AddressRequest {
    private String number;
    private String street;
    private String district;
    private String city;

}
