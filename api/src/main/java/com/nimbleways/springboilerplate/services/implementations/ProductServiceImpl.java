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
    public void handleNormalProduct(Product product) {
        if (product.getAvailable() > 0) {
            product.setAvailable(product.getAvailable() - 1);
            productRepository.save(product);
        } else {
            int leadTime = product.getLeadTime();
            if (leadTime > 0) {
                notifyDelay(leadTime, product);
            }
        }
    }

    @Override
    public void handleSeasonalProduct(Product product) {
        LocalDate now = LocalDate.now();
        if (isInSeason(product) && product.getAvailable() > 0) {
            product.setAvailable(product.getAvailable() - 1);
            productRepository.save(product);
        } else {
            handleSeasonalProductLogic(product);
        }
    }

    @Override
    public void handleExpiredProduct(Product product) {
        if (product.getAvailable() > 0 && !isExpired(product)) {
            product.setAvailable(product.getAvailable() - 1);
            productRepository.save(product);
        } else {
            notificationService.sendExpirationNotification(product.getName(), product.getExpiryDate());
            handleExpiredProductLogic(product);
        }
    }

    private boolean isInSeason(Product product) {
        LocalDate now = LocalDate.now();
        return now.isAfter(product.getSeasonStartDate()) && now.isBefore(product.getSeasonEndDate());
    }

    private boolean isExpired(Product product) {
        return LocalDate.now().isAfter(product.getExpiryDate());
    }

    private void notifyDelay(int leadTime, Product product) {
        try {
            product.setLeadTime(leadTime);
            productRepository.save(product);
            notificationService.sendDelayNotification(leadTime, product.getName());
        } catch (Exception e) {
            System.err.println("Failed to notify delay for product: " + product.getName());
            throw new RuntimeException("Failed to notify delay", e);
        }
    }

    private void handleSeasonalProductLogic(Product product) {
        if (LocalDate.now().plusDays(product.getLeadTime()).isAfter(product.getSeasonEndDate())) {
            markOutOfStock(product, "Season has ended");
        } else if (product.getSeasonStartDate().isAfter(LocalDate.now())) {
            markOutOfStock(product, "Season not started yet");
        } else {
            notifyDelay(product.getLeadTime(), product);
        }
    }

    private void handleExpiredProductLogic(Product product) {
        markOutOfStock(product, "Product expired");
    }

    private void markOutOfStock(Product product, String reason) {
        try {
            product.setAvailable(0);
            productRepository.save(product);
            notificationService.sendOutOfStockNotification(product.getName() + " (" + reason + ")");
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
