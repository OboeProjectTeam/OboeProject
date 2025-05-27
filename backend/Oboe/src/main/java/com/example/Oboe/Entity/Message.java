package com.example.Oboe.Entity;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

public class Message {
    // Message
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver;

}
