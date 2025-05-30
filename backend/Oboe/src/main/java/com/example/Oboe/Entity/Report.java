package com.example.Oboe.Entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "Reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "reportID", updatable = false, nullable = false)
    private UUID reportID;

    private String title;
    private String content;
    private LocalDate report_at = LocalDate.now();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
