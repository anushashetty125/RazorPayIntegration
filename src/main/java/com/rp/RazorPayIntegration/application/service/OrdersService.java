package com.rp.RazorPayIntegration.application.service;

import com.razorpay.RazorpayException;
import com.rp.RazorPayIntegration.entity.Orders;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface OrdersService {
    ResponseEntity<Orders> createOrder(Orders orders) throws RazorpayException;

    void updateOrderStatus(Map<String, String> response);
}
