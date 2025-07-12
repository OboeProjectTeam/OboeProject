package com.example.Oboe.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Bật một "simple broker" dùng cho việc gửi tin nhắn từ server → client
        // Tất cả các tin nhắn gửi đến client sẽ được gửi qua những địa chỉ bắt đầu bằng "/receiver" ,thông báo cũng vậy
        config.enableSimpleBroker("/receiver","/notification");
        // Định nghĩa prefix cho các địa chỉ client sẽ gửi message tới server
        // Ví dụ client gửi tới "/app/chat", thì controller sẽ có @MessageMapping("/chat")
        config.setApplicationDestinationPrefixes("/app");
    }


    // Cấu hình endpoint để client kết nối WebSocket
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Khai báo endpoint WebSocket mà client sẽ kết nối tới
        // client gọi tới /ws để mở kết nối
        registry.addEndpoint("/ws")

                // Cho phép mọi origin (domain) gọi tới (tránh lỗi CORS khi phát triển frontend riêng)
                .setAllowedOriginPatterns("*")

                // Kích hoạt SockJS: fallback khi trình duyệt không hỗ trợ WebSocket gốc
                .withSockJS();
    }
}


