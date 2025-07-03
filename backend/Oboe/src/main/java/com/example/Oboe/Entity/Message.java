package com.example.Oboe.Entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="Message")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "MessageID", updatable = false, nullable = false)
    private UUID MessageID;

    private String sent_message;


    private LocalDateTime sent_at = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.sent_at = LocalDateTime.now();
    }

    // Message
    @ManyToOne
    @JoinColumn(name = "SenderID")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "ReceiverID")
    private User receiver;

    public UUID getSenderId() {
        return sender != null ? sender.getUser_id() : null;
    }

    public UUID getReceiverId() {
        return receiver != null ? receiver.getUser_id() : null;
    }

    public UUID getMessageID() {
        return MessageID;
    }

    public void setMessageID(UUID messageID) {
        MessageID = messageID;
    }

    public String getSent_message() {
        return sent_message;
    }

    public void setSent_message(String sent_message) {
        this.sent_message = sent_message;
    }




}
