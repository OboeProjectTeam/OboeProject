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
    private Grammar gramma;

    @ManyToOne
    @JoinColumn(name ="kanjiId")
    private Kanji kanji;

    @ManyToOne
    @JoinColumn(name ="CardId")
    private FlashCards flashCards;

    @ManyToOne
    @JoinColumn(name ="Vocalb_id")
    private Vocabulary vocabulary;

    @ManyToOne
    @JoinColumn(name = "sample_sentence_id")
    private SampleSentence sentence;

    public SampleSentence getSentence() {
        return sentence;
    }

    public void setSentence(SampleSentence sentence) {
        this.sentence = sentence;
    }



    //Contructor
    public Favorites() {

    }

    public FlashCards getFlashCards() {
        return flashCards;
    }

    public void setFlashCards(FlashCards flashCards) {
        this.flashCards = flashCards;
    }

    public UUID getFavoritesID() {
        return FavoritesID;
    }

    public void setFavoritesID(UUID favoritesID) {
        FavoritesID = favoritesID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDate getFavories_at() {
        return favories_at;
    }

    public void setFavories_at(LocalDate favories_at) {
        this.favories_at = favories_at;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Grammar getGramma() {
        return gramma;
    }

    public void setGramma(Grammar gramma) {
        this.gramma = gramma;
    }

    public Kanji getKanji() {
        return kanji;
    }

    public void setKanji(Kanji kanji) {
        this.kanji = kanji;
    }

    public Vocabulary getVocabulary() {
        return vocabulary;
    }

    public void setVocabulary(Vocabulary vocabulary) {
        this.vocabulary = vocabulary;
    }




}
