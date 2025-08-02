package com.example.Oboe.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name="Payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "PaymentID", updatable = false, nullable = false)
    private UUID PaymentID;
    private int amount;
    private String status;
    private LocalDate paymentDate = LocalDate.now();
    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "payment_method")
    private String paymentMethod;
    @Column(name = "order_code")
    private Long orderCode;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference("users-Payment")
    private User user;
    @Column(name = "paid_at")
    private LocalDateTime paidAt;


    public Payment() {}

    public Payment(int amount, String status, User user) {
        this.amount = amount;
        this.status = status;
        this.user = user;
    }
    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Long getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(Long orderCode) {
        this.orderCode = orderCode;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public UUID getPaymentID() {
        return PaymentID;
    }

    public void setPaymentID(UUID paymentID) {
        PaymentID = paymentID;
    }


    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
