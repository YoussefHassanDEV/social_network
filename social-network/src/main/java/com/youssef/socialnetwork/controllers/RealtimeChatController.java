package com.youssef.socialnetwork.controllers;

import com.youssef.socialnetwork.dto.GroupChatMessage;
import com.youssef.socialnetwork.dto.PrivateChatMessage;
import com.youssef.socialnetwork.model.User;
import com.youssef.socialnetwork.repository.UserRepository;
import com.youssef.socialnetwork.service.GroupService;
import com.youssef.socialnetwork.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class RealtimeChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepo;
    private final MessageService messageService;       // your existing 1:1 service
    private final GroupService groupService;           // from previous step

    // 1:1 SEND: client sends to /app/private.send
    @MessageMapping("/private.send")
    public void handlePrivate(Principal principal, PrivateChatMessage payload) {
        String senderUsername = principal.getName();
        User sender = userRepo.findByUsername(senderUsername).orElseThrow();
        Long receiverId = payload.getReceiverId();

        // persist
        var saved = messageService.sendMessage(sender.getId(), receiverId, payload.getContent());

        // deliver to receiver's personal queue
        messagingTemplate.convertAndSendToUser(
                String.valueOf(receiverId), "/queue/private", saved
        );

        // (optional) echo to sender (to mark delivered)
        messagingTemplate.convertAndSendToUser(
                String.valueOf(sender.getId()), "/queue/private", saved
        );
    }

    // GROUP SEND: client sends to /app/group.send
    @MessageMapping("/group.send")
    public void handleGroup(Principal principal, GroupChatMessage payload) {
        String senderUsername = principal.getName();
        User sender = userRepo.findByUsername(senderUsername).orElseThrow();

        var savedDto = groupService.sendMessage(payload.getGroupId(), sender.getId(), payload.getContent());

        // broadcast to group topic subscribers
        messagingTemplate.convertAndSend("/topic/group." + payload.getGroupId(), savedDto);
    }
}
