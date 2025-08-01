package com.example.Oboe.Repository;

import com.example.Oboe.Entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Payment findByTransactionId(String transactionId);
    Payment findByOrderCode(Long orderCode);
}

