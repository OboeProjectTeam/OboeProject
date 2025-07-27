package com.example.Oboe.websocket;

import com.example.Oboe.Service.SessionManager;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.UUID;

@Component
public class MyRawSocketHandler extends TextWebSocketHandler {

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String query = session.getUri().getQuery();
        if (query == null || !query.startsWith("userId=")) {
            System.out.println(" Missing or invalid userId query param. Closing session.");
            session.close();
            return;
        }
        try {
            UUID userId = UUID.fromString(query.split("=")[1]);
            SessionManager.addSession(userId, session);
            System.out.println(" Raw WebSocket connected: " + session.getId() + " for user " + userId);
        } catch (IllegalArgumentException ex) {
            System.out.println(" Invalid UUID: " + query);
            session.close();
        }
    }
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println(" Received: " + message.getPayload());
        session.sendMessage(new TextMessage("You said: " + message.getPayload()));
    }
}
