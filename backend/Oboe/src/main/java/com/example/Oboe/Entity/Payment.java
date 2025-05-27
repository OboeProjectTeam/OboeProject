package com.example.Oboe.Entity;

import jakarta.persistence.*;

import java.util.UUID;


@Entity
@Table(name="Payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID PaymentID;
        
}
