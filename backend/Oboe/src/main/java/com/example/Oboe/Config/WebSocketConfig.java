package com.example.Oboe.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Bật broker cho các kênh mà server sẽ gửi dữ liệu tới client:
        config.enableSimpleBroker(
                "/receiver",        // Gửi tin nhắn cá nhân
                "/notification",    // Gửi thông báo hệ thống
                "/blog"             // Gửi bình luận blog theo blogId
        );

        // Prefix client gửi tới server (ví dụ: /app/chat)
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint để client kết nối websocket (gọi tới /ws)
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // Cho phép mọi domain (CORS)
                .withSockJS();                 // Hỗ trợ SockJS fallback
    }
}
