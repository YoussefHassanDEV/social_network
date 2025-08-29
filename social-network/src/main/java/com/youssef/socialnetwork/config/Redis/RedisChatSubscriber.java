package com.youssef.socialnetwork.config.Redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisChatSubscriber implements MessageListener {

    private final SimpMessagingTemplate messagingTemplate; // sends to WebSocket clients

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String msg = new String(message.getBody());
            log.info("📩 Received message from Redis: {}", msg);

            // forward to WebSocket topic
            messagingTemplate.convertAndSend("/topic/chat", msg);
        } catch (Exception e) {
            log.error("Error handling Redis message", e);
        }
    }
}
