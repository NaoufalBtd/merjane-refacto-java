package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.services.OrderService;
import com.nimbleways.springboilerplate.services.facade.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductService productService;

    @Override
    public ProcessOrderResponse processOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        Set<Product> products = order.getItems();
        for (Product product : products) {
            processProduct(product);
        }

        return new ProcessOrderResponse(order.getId());
    }

    private void processProduct(Product product) {
        switch (product.getType()) {
            case "NORMAL":
                handleNormalProduct(product);
                break;
            case "SEASONAL":
                productService.handleSeasonalProduct(product);
                break;
            case "EXPIRABLE":
                productService.handleExpiredProduct(product);
                break;
            default:
                throw new IllegalArgumentException("Unknown product type: " + product.getType());
        }
    }

    private void handleNormalProduct(Product product) {
        if (product.getAvailable() > 0) {
            product.setAvailable(product.getAvailable() - 1);
        } else {
            productService.notifyDelay(product.getLeadTime(), product);
        }
    }
}