package com.example.Oboe.Service;

import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private static final Map<UUID, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public static void addSession(UUID userId, WebSocketSession session) {
        sessions.put(userId, session);
    }

    public static void removeSession(UUID userId) {
        sessions.remove(userId);
    }

    public static WebSocketSession getSession(UUID userId) {
        return sessions.get(userId);
    }
    public static Map<UUID, WebSocketSession> getAllSessions() {
        return sessions;
    }


}
