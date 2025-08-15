package vn.edu.iuh.fit.bookshop_be.dtos;

import lombok.Data;

@Data
public class CategoryRequest {
    private String name;
    private String description;
}
