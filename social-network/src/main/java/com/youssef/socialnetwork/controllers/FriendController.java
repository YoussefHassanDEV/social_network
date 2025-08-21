package com.youssef.socialnetwork.controllers;

import com.youssef.socialnetwork.dto.FriendshipResponseDTO;
import com.youssef.socialnetwork.model.Friendship;
import com.youssef.socialnetwork.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    private FriendshipResponseDTO toDto(Friendship f) {
        return FriendshipResponseDTO.builder()
                .id(f.getId())
                .requesterId(f.getRequester().getId())
                .requesterName(f.getRequester().getUsername())
                .receiverId(f.getReceiver().getId())
                .receiverName(f.getReceiver().getUsername())
                .status(f.getStatus())
                .build();
    }

    @PostMapping("/send/{requesterId}/{receiverId}")
    public ResponseEntity<FriendshipResponseDTO> sendRequest(
            @PathVariable Long requesterId,
            @PathVariable Long receiverId) {
        return ResponseEntity.ok(toDto(friendService.sendRequest(requesterId, receiverId)));
    }

    @PostMapping("/accept/{friendshipId}")
    public ResponseEntity<FriendshipResponseDTO> acceptRequest(@PathVariable Long friendshipId) {
        return ResponseEntity.ok(toDto(friendService.acceptRequest(friendshipId)));
    }

    @PostMapping("/reject/{friendshipId}")
    public ResponseEntity<FriendshipResponseDTO> rejectRequest(@PathVariable Long friendshipId) {
        return ResponseEntity.ok(toDto(friendService.rejectRequest(friendshipId)));
    }

    @PostMapping("/block/{friendshipId}")
    public ResponseEntity<FriendshipResponseDTO> blockUser(@PathVariable Long friendshipId) {
        return ResponseEntity.ok(toDto(friendService.blockUser(friendshipId)));
    }

    @GetMapping("/list/{userId}")
    public ResponseEntity<List<FriendshipResponseDTO>> listFriends(@PathVariable Long userId) {
        return ResponseEntity.ok(friendService.listFriends(userId)
                .stream().map(this::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/pending/received/{userId}")
    public ResponseEntity<List<FriendshipResponseDTO>> pendingReceived(@PathVariable Long userId) {
        return ResponseEntity.ok(friendService.listPendingRequests(userId)
                .stream().map(this::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/pending/sent/{userId}")
    public ResponseEntity<List<FriendshipResponseDTO>> pendingSent(@PathVariable Long userId) {
        return ResponseEntity.ok(friendService.listSentRequests(userId)
                .stream().map(this::toDto).collect(Collectors.toList()));
    }
}
