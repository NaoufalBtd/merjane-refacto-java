package com.nimbleways.springboilerplate.mappers;

import com.nimbleways.springboilerplate.dto.product.ProductRequestDto;
import com.nimbleways.springboilerplate.entities.Product;

public class ProductMapper {
    public static ProductRequestDto toDto(Product product) {
        if (product == null) {
            return null;
        }
        ProductRequestDto dto = new ProductRequestDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setAvailable(product.getAvailable());
        dto.setLeadTime(product.getLeadTime());
        dto.setType(product.getType());
        dto.setSeasonStartDate(product.getSeasonStartDate());
        dto.setSeasonEndDate(product.getSeasonEndDate());
        dto.setExpiryDate(product.getExpiryDate());
        return dto;
    }

    public static Product toEntity(ProductRequestDto dto) {
        if (dto == null) {
            return null;
        }
        Product product = new Product();
        product.setId(dto.getId());
        product.setName(dto.getName());
        product.setAvailable(dto.getAvailable());
        product.setLeadTime(dto.getLeadTime());
        product.setType(dto.getType());
        product.setSeasonStartDate(dto.getSeasonStartDate());
        product.setSeasonEndDate(dto.getSeasonEndDate());
        product.setExpiryDate(dto.getExpiryDate());
        return product;
    }
}
