package com.youssef.socialnetwork.config.Websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Client connects here, e.g. ws://host/ws (or http -> SockJS)
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS(); // keep if you want SockJS fallback; remove if pure ws only
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Client SENDs to /app/**
        registry.setApplicationDestinationPrefixes("/app");

        // Server BROADCASTs to these
        registry.enableSimpleBroker("/topic", "/queue");
        // Per-user (for 1:1) will go to /user/queue/**
        registry.setUserDestinationPrefix("/user");
    }
}
