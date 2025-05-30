package com.example.Oboe.Entity;


import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name="Topic")
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "TopicId", updatable = false, nullable = false)
    private UUID TopicId;
    private String TopicName;
    private String TopicDescription;
}
