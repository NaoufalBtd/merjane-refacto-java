package com.nimbleways.springboilerplate.controllers;

import com.nimbleways.springboilerplate.dto.product.ProductRequestDto;
import com.nimbleways.springboilerplate.mappers.ProductMapper;
import com.nimbleways.springboilerplate.services.facade.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/notifyDelay")
    @ResponseStatus(HttpStatus.OK)
    public void notifyDelay(@RequestBody ProductRequestDto productDto) {
        productService.notifyDelay(productDto.getLeadTime(), ProductMapper.toEntity(productDto));
    }

    @PostMapping("/handleSeasonal")
    @ResponseStatus(HttpStatus.OK)
    public void handleSeasonalProduct(@RequestBody ProductRequestDto productDto) {
        productService.handleSeasonalProduct(ProductMapper.toEntity(productDto));
    }

    @PostMapping("/handleExpired")
    @ResponseStatus(HttpStatus.OK)
    public void handleExpiredProduct(@RequestBody ProductRequestDto productDto) {
        productService.handleExpiredProduct(ProductMapper.toEntity(productDto));
    }

}
