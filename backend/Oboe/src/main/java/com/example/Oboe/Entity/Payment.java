package com.example.Oboe.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;


@Entity
@Table(name="Payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "PaymentID", updatable = false, nullable = false)
    private UUID PaymentID;
    private String amount;
    private String status;
    private LocalDate paymentDate = LocalDate.now();

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference("users-Payment")
    private User user;

    public Payment() {}

    public Payment(String amount, String status, User user) {
        this.amount = amount;
        this.status = status;
        this.user = user;
    }
}
