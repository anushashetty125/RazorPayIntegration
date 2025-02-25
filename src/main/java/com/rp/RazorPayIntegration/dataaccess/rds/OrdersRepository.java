package com.rp.RazorPayIntegration.dataaccess.rds;

import com.rp.RazorPayIntegration.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Integer> {
    Optional<Orders> findByRazorpayOrderId(String razorpayId);
}
