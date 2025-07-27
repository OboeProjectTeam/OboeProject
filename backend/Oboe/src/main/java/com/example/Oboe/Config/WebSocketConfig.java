package com.example.Oboe.Config;

import com.example.Oboe.websocket.MyRawSocketHandler;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.beans.factory.annotation.Autowired;


@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private MyRawSocketHandler myRawSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(myRawSocketHandler, "/ws-raw")
                .setAllowedOrigins("*");
    }
    @PostConstruct
    public void logInit() {
        System.out.println(" WebSocketConfig (Raw) initialized: /ws-raw endpoint ready.");
    }
}
