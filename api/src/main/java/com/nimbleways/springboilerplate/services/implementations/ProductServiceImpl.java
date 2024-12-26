package com.nimbleways.springboilerplate.services.implementations;

import java.time.LocalDate;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.facade.NotificationService;
import com.nimbleways.springboilerplate.services.facade.ProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private NotificationService notificationService;

    @Override
    public void notifyDelay(int leadTime, Product product) {
        validateProduct(product);
        try {
            product.setLeadTime(leadTime);
            productRepository.save(product);
            notificationService.sendDelayNotification(leadTime, product.getName());
        } catch (Exception e) {
            System.err.println("Failed to notify delay for product: " + product.getName());
            throw new RuntimeException("Failed to notify delay", e);
        }
    }

    @Override
    public void handleSeasonalProduct(Product product) {
        validateProduct(product);
        try {
            LocalDate now = LocalDate.now();
            if (now.plusDays(product.getLeadTime()).isAfter(product.getSeasonEndDate())) {
                markOutOfStock(product, "Season has ended");
            } else if (product.getSeasonStartDate().isAfter(now)) {
                markOutOfStock(product, "Season not started yet");
            } else {
                notifyDelay(product.getLeadTime(), product);
            }
        } catch (Exception e) {
            System.err.println("Failed to handle seasonal product: " + product.getName());
            throw new RuntimeException("Failed to handle seasonal product", e);
        }
    }

    @Override
    public void handleExpiredProduct(Product product) {
        validateProduct(product);
        try {
            if (product.getAvailable() > 0 && product.getExpiryDate().isAfter(LocalDate.now())) {
                product.setAvailable(product.getAvailable() - 1);
            } else {
                notificationService.sendExpirationNotification(product.getName(), product.getExpiryDate());
                markOutOfStock(product, "Product expired");
            }
            productRepository.save(product);
        } catch (Exception e) {
            System.err.println("Failed to handle expired product: " + product.getName());
            throw new RuntimeException("Failed to handle expired product", e);
        }
    }

    private void markOutOfStock(Product product, String reason) {
        try {
            product.setAvailable(0);
            productRepository.save(product);
            notificationService.sendOutOfStockNotification(product.getName() + " (" + reason + ")");
            System.out.println("Product marked out of stock: " + product.getName() + " (" + reason + ")");
        } catch (Exception e) {
            System.err.println("Failed to mark product as out of stock: " + product.getName());
            throw new RuntimeException("Failed to mark product as out of stock", e);
        }
    }

    private void validateProduct(Product product) {
        if (product == null) {
            System.err.println("Product is null");
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (product.getName() == null || product.getName().isEmpty()) {
            System.err.println("Invalid product name for product: " + product);
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
    }
}
