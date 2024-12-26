package com.nimbleways.springboilerplate.dto.product;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ProductDto {
    private Long id;
    private String name;
    private Integer available;
    private Integer leadTime;
    private String type;
    private LocalDate seasonStartDate;
    private LocalDate seasonEndDate;
    private LocalDate expiryDate;
}
