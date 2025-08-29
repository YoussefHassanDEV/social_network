package com.youssef.socialnetwork.controllers;

import com.youssef.socialnetwork.dto.GroupMessageDTO;
import com.youssef.socialnetwork.model.ChatGroup;
import com.youssef.socialnetwork.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping("/create")
    public ResponseEntity<ChatGroup> createGroup(@RequestParam String name,
                                                 @RequestBody List<Long> memberIds) {
        return ResponseEntity.ok(groupService.createGroup(name, memberIds));
    }

    @PostMapping("/{groupId}/add-member/{userId}")
    public ResponseEntity<Void> addMember(@PathVariable Long groupId, @PathVariable Long userId) {
        groupService.addMember(groupId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{groupId}/send/{senderId}")
    public ResponseEntity<GroupMessageDTO> sendMessage(@PathVariable Long groupId,
                                                       @PathVariable Long senderId,
                                                       @RequestBody String content) {
        return ResponseEntity.ok(groupService.sendMessage(groupId, senderId, content));
    }

    @GetMapping("/{groupId}/messages")
    public ResponseEntity<List<GroupMessageDTO>> getMessages(@PathVariable Long groupId) {
        return ResponseEntity.ok(groupService.getMessages(groupId));
    }
}
