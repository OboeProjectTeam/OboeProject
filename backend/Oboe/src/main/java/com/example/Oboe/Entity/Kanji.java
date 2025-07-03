package com.example.Oboe.Entity;

import com.example.Oboe.Entity.*;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "kanji")
public class Kanji {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "kanjiId", updatable = false, nullable = false)
    private UUID kanjiId;

    @Column(nullable = false)
    private String character_name; // Ký tự Kanji

    private String meaning;

    private String strokes;  // Số nét


}
