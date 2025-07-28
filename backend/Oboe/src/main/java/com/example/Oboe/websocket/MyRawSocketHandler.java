package com.example.Oboe.websocket;

import com.example.Oboe.Service.SessionManager;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.UUID;

// Đánh dấu đây là một bean component để Spring tự quản lý
@Component
public class MyRawSocketHandler extends TextWebSocketHandler {

    // Gọi khi client kết nối WebSocket thành công
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Lấy query param từ URL, ví dụ: ws://localhost:8080/ws?userId=abc
        String query = session.getUri().getQuery();

        // Kiểm tra nếu không có userId thì đóng kết nối
        if (query == null || !query.startsWith("userId=")) {
            System.out.println(" Missing or invalid userId query param. Closing session.");
            session.close();
            return;
        }

        try {
            // Parse userId từ chuỗi query
            UUID userId = UUID.fromString(query.split("=")[1]);

            // Lưu session của user vào SessionManager để tiện truy cập sau này
            SessionManager.addSession(userId, session);
            System.out.println(" Raw WebSocket connected: " + session.getId() + " for user " + userId);
        } catch (IllegalArgumentException ex) {
            // Nếu chuỗi không phải UUID hợp lệ, đóng kết nối
            System.out.println(" Invalid UUID: " + query);
            session.close();
        }
    }

    // Gọi khi server nhận được tin nhắn văn bản từ client
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println(" Received: " + message.getPayload());

        // Gửi lại cho client phản hồi (echo)
        session.sendMessage(new TextMessage("You said: " + message.getPayload()));
    }

    // Gọi khi client đóng kết nối WebSocket
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        // Lấy userId từ SessionManager bằng cách duyệt tất cả session
        UUID foundUser = null;
        for (Map.Entry<UUID, WebSocketSession> entry : SessionManager.getAllSessions().entrySet()) {
            if (entry.getValue().getId().equals(session.getId())) {
                foundUser = entry.getKey();
                break;
            }
        }
        // Nếu tìm được userId thì xóa session
        if (foundUser != null) {
            SessionManager.removeSession(foundUser);
            System.out.println("Connection closed: " + session.getId() + " for user: " + foundUser);
        } else {
            System.out.println("Connection closed: " + session.getId() + " (user not found)");
        }
    }

}
