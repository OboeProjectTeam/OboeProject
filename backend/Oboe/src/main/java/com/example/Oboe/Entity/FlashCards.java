package com.example.Oboe.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="FlashCards")
public class FlashCards {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "set_id", updatable = false, nullable = false)
    private UUID set_id;

    private String term; // Tên bộ thẻ
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference("user-flashcards")
    private User user;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime created;

    @OneToMany(mappedBy = "flashCards", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CardItem> cardItems = new ArrayList<>();

    public FlashCards() {}
    public FlashCards(String term, String description, User user) {
        this.term = term;
        this.description = description;
        this.user = user;
    }

    public UUID getSet_id() {
        return set_id;
    }

    public void setSet_id(UUID set_id) {
        this.set_id = set_id;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public List<CardItem> getCardItems() {
        return cardItems;
    }

    public void setCardItems(List<CardItem> cardItems) {
        this.cardItems = cardItems;
    }
}
