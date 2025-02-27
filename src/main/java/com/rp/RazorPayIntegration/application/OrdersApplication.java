package com.rp.RazorPayIntegration.application;

import com.razorpay.RazorpayException;
import com.rp.RazorPayIntegration.application.service.OrdersService;
import com.rp.RazorPayIntegration.controller.response.KeyResponse;
import com.rp.RazorPayIntegration.entity.Orders;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class OrdersApplication {
    private final OrdersService ordersService;

    public ResponseEntity<Orders> createOrder(Orders orders) throws RazorpayException {
        return ordersService.createOrder(orders);
    }

    public void updateOrderStatus(Map<String, String> response) {
        ordersService.updateOrderStatus(response);
    }

    public ResponseEntity<KeyResponse> getKey() {
        return ordersService.getKey();
    }

    public ResponseEntity<String> updateOrderStatusByWebhook(String response, String signature) {
        return ordersService.updateOrderStatusByWebhook(response,signature);
    }
}
