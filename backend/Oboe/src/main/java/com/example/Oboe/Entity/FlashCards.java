package com.example.Oboe.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="FlashCards")
public class FlashCards {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "CardId", updatable = false, nullable = false)
    private UUID CardId;
    private String Term;
    private String Description;
    private String ImageUrl;
    private String AudioUrl;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference("users-FlashCards")
    private User user;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "LevelId", nullable = false)
    @JsonBackReference("Level-FlashCards")
    private Level level;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name="TopicId",nullable = false)
    @JsonBackReference("Topic-FlashCards")
    private Topic topic;
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime Created = LocalDateTime.now();

    public FlashCards() {}
    public FlashCards(String Term, String Description, String ImageUrl, String AudioUrl, User user,Level level, Topic topic) {
        this.Term = Term;
        this.Description = Description;
        this.ImageUrl = ImageUrl;
        this.AudioUrl = AudioUrl;
        this.user = user;
        this.level = level;
        this.topic = topic;
    }

    public UUID getCardId() {
        return CardId;
    }

    public void setCardId(UUID cardId) {
        CardId = cardId;
    }

    public String getTerm() {
        return Term;
    }

    public void setTerm(String term) {
        Term = term;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getAudioUrl() {
        return AudioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        AudioUrl = audioUrl;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public LocalDateTime getCreated() {
        return Created;
    }

    public void setCreated(LocalDateTime created) {
        Created = created;
    }
}
