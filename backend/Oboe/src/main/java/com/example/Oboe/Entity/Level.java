package com.example.Oboe.Entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name ="Level")
public class Level {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "LevelId", updatable = false, nullable = false)
    private UUID LevelId;
    private String LevelName;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference("users-Level")
    private User user;

    public Level() {
    }
    public Level(String LevelName, User user) {
        this.LevelName = LevelName;
        this.user = user;
    }

    public UUID getLevelId() {
        return LevelId;
    }

    public void setLevelId(UUID levelId) {
        LevelId = levelId;
    }

    public String getLevelName() {
        return LevelName;
    }

    public void setLevelName(String levelName) {
        LevelName = levelName;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
