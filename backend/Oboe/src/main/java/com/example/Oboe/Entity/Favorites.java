package com.example.Oboe.Entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "Favorites")
public class Favorites {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "FavoritesID", updatable = false, nullable = false)
    private UUID FavoritesID;

    private String title;
    private String content;
    private LocalDate favories_at = LocalDate.now();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name ="grammaID")
    private Gramma gramma;

    @ManyToOne
    @JoinColumn(name ="kanjiId")
    private Kanji kanji;

    @ManyToOne
    @JoinColumn(name ="CardId")
    private FlashCards flashCards;

    @ManyToOne
    @JoinColumn(name ="Vocalb_id")
    private Vocabulary vocabulary;


}
