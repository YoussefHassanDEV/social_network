package com.youssef.socialnetwork.controllers;

import com.youssef.socialnetwork.dto.MessageResponseDTO;
import com.youssef.socialnetwork.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/send/{senderId}/{receiverId}")
    public ResponseEntity<MessageResponseDTO> sendMessage(@PathVariable Long senderId,
                                                          @PathVariable Long receiverId,
                                                          @RequestBody String content) {
        return ResponseEntity.ok(messageService.sendMessage(senderId, receiverId, content));
    }

    @GetMapping("/conversation/{user1Id}/{user2Id}")
    public ResponseEntity<List<MessageResponseDTO>> getConversation(@PathVariable Long user1Id,
                                                                    @PathVariable Long user2Id,
                                                                    @RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(messageService.getConversation(user1Id, user2Id, page, size));
    }

    @DeleteMapping("/{messageId}/{userId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long messageId,
                                              @PathVariable Long userId) {
        messageService.deleteMessage(messageId, userId);
        return ResponseEntity.ok().build();
    }
}
