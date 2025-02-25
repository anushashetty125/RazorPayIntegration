package com.rp.RazorPayIntegration.application.service.impl;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.rp.RazorPayIntegration.application.service.OrdersService;
import com.rp.RazorPayIntegration.dataaccess.rds.OrdersRepository;
import com.rp.RazorPayIntegration.entity.Orders;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class OrdersServiceImpl implements OrdersService {
    private final OrdersRepository ordersRepository;

    @Value("${razorpay.key.id}")
    private String razorpayId;
    @Value("${razorpay.key.secret}")
    private String razorpaySecret;

    public ResponseEntity<Orders> createOrder(Orders orders) throws RazorpayException {
        RazorpayClient razorpayClient = new RazorpayClient(razorpayId,razorpaySecret);
        JSONObject jsonObject = new JSONObject();
        //Convert to paisa
        jsonObject.put("amount",orders.getAmount() * 100);
        jsonObject.put("currency","INR");
        jsonObject.put("receipt",orders.getEmail());
        val razorPayOrderObj = razorpayClient.orders.create(jsonObject);
        orders.setRazorpayOrderId(razorPayOrderObj.get("id"));
        orders.setPaymentStatus(razorPayOrderObj.get("status"));
        Orders ordersObj = ordersRepository.save(orders);
        return new ResponseEntity<>(ordersObj, HttpStatus.CREATED);
    }

    @Override
    public void updateOrderStatus(Map<String, String> response) {
        String razorpayId = response.get("razorpay_order_id");
        val order = ordersRepository.findByRazorpayOrderId(razorpayId);
        order.ifPresent(orders -> orders.setPaymentStatus("PAID"));
        ordersRepository.save(order.get());
    }
}
