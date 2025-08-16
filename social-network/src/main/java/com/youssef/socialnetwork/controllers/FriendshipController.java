package com.youssef.socialnetwork.controllers;


import com.youssef.socialnetwork.Enums.FriendshipStatus;
import com.youssef.socialnetwork.model.Friendship;
import com.youssef.socialnetwork.model.User;
import com.youssef.socialnetwork.repository.FriendshipRepository;
import com.youssef.socialnetwork.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendshipController {

    private final FriendshipRepository friendships;
    private final UserRepository users;

    // Send a friend request
    @PostMapping("/add/{username}")
    public ResponseEntity<?> sendRequest(@PathVariable String username, Principal principal) {
        var requester = users.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        var receiver = users.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (friendships.findByRequesterAndReceiver(requester, receiver).isPresent()) {
            return ResponseEntity.badRequest().body("Request already exists");
        }

        var friendship = Friendship.builder()
                .requester(requester)
                .receiver(receiver)
                .status(FriendshipStatus.PENDING)
                .build();

        friendships.save(friendship);
        return ResponseEntity.ok("Friend request sent");
    }

    // Accept a friend request
    @PostMapping("/accept/{username}")
    public ResponseEntity<?> acceptRequest(@PathVariable String username, Principal principal) {
        var receiver = users.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        var requester = users.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        var friendship = friendships.findByRequesterAndReceiver(requester, receiver)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        friendship.setStatus(FriendshipStatus.ACCEPTED);
        friendships.save(friendship);

        return ResponseEntity.ok("Friend request accepted");
    }

    // Block a user
    @PostMapping("/block/{username}")
    public ResponseEntity<?> blockUser(@PathVariable String username, Principal principal) {
        var me = users.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        var other = users.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        var friendship = friendships.findByRequesterAndReceiver(me, other)
                .orElse(Friendship.builder()
                        .requester(me)
                        .receiver(other)
                        .status(FriendshipStatus.BLOCKED)
                        .build());

        friendship.setStatus(FriendshipStatus.BLOCKED);
        friendships.save(friendship);

        return ResponseEntity.ok("User blocked");
    }

    // Get my friends' list
    @GetMapping
    public ResponseEntity<List<User>> getFriends(Principal principal) {
        var me = users.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        var sent = friendships.findAllByRequesterAndStatus(me, FriendshipStatus.ACCEPTED)
                .stream().map(Friendship::getReceiver).toList();

        var received = friendships.findAllByReceiverAndStatus(me, FriendshipStatus.ACCEPTED)
                .stream().map(Friendship::getRequester).toList();

        sent.addAll(received);
        return ResponseEntity.ok(sent);
    }
}
