package com.rp.RazorPayIntegration.controller;

import com.razorpay.RazorpayException;
import com.rp.RazorPayIntegration.application.OrdersApplication;
import com.rp.RazorPayIntegration.controller.response.KeyResponse;
import com.rp.RazorPayIntegration.entity.Orders;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
@CrossOrigin(origins = "https://406b-202-12-82-169.ngrok-free.app")
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
        System.out.println("success called");
//        ordersApplication.updateOrderStatus(response);
        return "success";
    }

    @GetMapping(value = "/key", produces = "application/json")
    @ResponseBody
    public ResponseEntity<KeyResponse> getRazorpayKey(){
        return ordersApplication.getKey();
    }

    @PostMapping("/webhook-callback")
    public ResponseEntity<String> updateOrderStatusByWebhook(@RequestBody String response,
                                                             @RequestHeader("X-Razorpay-Signature") String signature){
        return ordersApplication.updateOrderStatusByWebhook(response,signature);
    }
}
