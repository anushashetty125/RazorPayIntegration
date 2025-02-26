package com.rp.RazorPayIntegration.entity;

import jakarta.persistence.*;
import lombok.Data;

import static com.rp.RazorPayIntegration.constants.SchemaName.ORDERS;

@Table(name = ORDERS)
@Entity
@Data
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String name;
    private String email;
    private Double amount;
    private String currency;
    private String paymentStatus;
    private String razorpayOrderId;
}
