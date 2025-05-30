package com.example.Oboe.Entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Materials")

public class Materials {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "materialID", updatable = false, nullable = false)
    private UUID materialID;

    @ManyToOne
    @JoinColumn(name = "User_Id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "Blogs_Id")
    private Blog blog;

    @ManyToOne
    @JoinColumn(name = "Messages_Id")
    private Message message;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(length = 255)
    private String description;

    @Column(name = "File_url", length = 255)
    private String fileUrl;

    @Column(name = "Uploaded_At")
    private LocalDateTime uploadedAt;

    @Column(length = 255)
    private String link;

}
