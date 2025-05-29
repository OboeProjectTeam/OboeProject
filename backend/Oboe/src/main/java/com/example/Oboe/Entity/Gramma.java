package com.example.Oboe.Entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "Gramma")
public class Gramma {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "grammaID", updatable = false, nullable = false)
    private UUID grammaID;
    private String structure;
    private String explanation;
    private String example;

    @ManyToOne
    @JoinColumn(name = "LevelID")
    private Level level;
}
