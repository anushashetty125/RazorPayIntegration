package com.rp.RazorPayIntegration.controller;

import com.razorpay.RazorpayException;
import com.rp.RazorPayIntegration.application.OrdersApplication;
import com.rp.RazorPayIntegration.entity.Orders;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class OrdersController {
    private final OrdersApplication ordersApplication;

    @GetMapping("/order")
    public String ordersPage(){
        return "order";
    }

    @PostMapping(value = "/create-order", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Orders> createOrder(@RequestBody Orders orders) throws RazorpayException {
        return ordersApplication.createOrder(orders);
    }
    @PostMapping("/paymentCallback")
    public String paymentCallback(@RequestParam Map<String, String> response) {
        ordersApplication.updateOrderStatus(response);
        return "success";
    }
}
