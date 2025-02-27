package com.rp.RazorPayIntegration.application.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import com.rp.RazorPayIntegration.application.service.OrdersService;
import com.rp.RazorPayIntegration.controller.response.KeyResponse;
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

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;

@Service
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class OrdersServiceImpl implements OrdersService {
    private final OrdersRepository ordersRepository;

    @Value("${razorpay.key.id}")
    private String razorpayId;
    @Value("${razorpay.key.secret}")
    private String razorpaySecret;
    @Value("${webhook.secret}")
    private String webhookSecret;

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
        if (order.isPresent()) {
            order.get().setPaymentStatus("PAID");
            ordersRepository.save(order.get());
        }
    }

    @Override
    public ResponseEntity<KeyResponse> getKey() {
        val keyResponse = KeyResponse.builder().key(razorpayId).build();
        return new ResponseEntity<>(keyResponse,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> updateOrderStatusByWebhook(String response, String signature) {
        System.out.println("webhook called");
        try {
            //validate signature
//            if (!isValidSignature(response,signature)) {
//                System.out.println("Signature validation failed");
//                return ResponseEntity.ok("Signature validation failed");
//            }
            Utils.verifyWebhookSignature(response,signature,webhookSecret);

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> responseMap = objectMapper.readValue(response,Map.class);

            String event = (String) responseMap.get("event");
            Map<String, Object> payload = (Map<String, Object>) responseMap.get("payload");
            Map<String, Object> payment = (Map<String, Object>) payload.get("payment");
            Map<String,Object> entity = (Map<String, Object>) payment.get("entity");

//            if ("payment.captured".equals(event) ) {
                String orderId = (String) entity.get("order_id");
                String status = (String) entity.get("status");

                //update order status in database
                val order = ordersRepository.findByRazorpayOrderId(orderId);
                if (order.isPresent()) {
                    order.get().setPaymentStatus(status);
                    ordersRepository.save(order.get());
                }
//            }
            System.out.println("Webhook triggered");
            return ResponseEntity.ok("Webhook Received successfully");
        } catch (JsonProcessingException e) {
            System.out.println("Webhook  not triggered");
            return ResponseEntity.ok("Webhook not triggered");
        }
        catch (RazorpayException e) {
            System.out.println("Signature validation failed");
            return ResponseEntity.ok("Signature validation failed");
        }
    }

    private boolean isValidSignature(String response, String signature) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256");
            sha256_HMAC.init(secretKeySpec);
            byte[] hash = sha256_HMAC.doFinal(response.getBytes(StandardCharsets.UTF_8));
            String expectedSignature = Base64.getEncoder().encodeToString(hash);
            System.out.println(expectedSignature);
            System.out.println(signature);
            System.out.println(webhookSecret);
            return expectedSignature.equals(webhookSecret);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            return false;
        }
    }
}
