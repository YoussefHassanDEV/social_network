package com.youssef.socialnetwork.controllers;

import com.youssef.socialnetwork.config.Redis.RedisChatPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final RedisChatPublisher publisher;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload String message) {
        // Publish to Redis instead of directly sending
        publisher.publish(message);
    }
}
