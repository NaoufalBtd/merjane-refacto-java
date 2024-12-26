package com.nimbleways.springboilerplate.services;

import com.nimbleways.springboilerplate.dto.order.ProcessOrderResponse;

public interface OrderService {
    ProcessOrderResponse processOrder(Long orderId);
}
