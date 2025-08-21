package com.youssef.socialnetwork.service;

import com.youssef.socialnetwork.Enums.FriendshipStatus;
import com.youssef.socialnetwork.model.Friendship;
import com.youssef.socialnetwork.model.User;
import com.youssef.socialnetwork.repository.FriendshipRepository;
import com.youssef.socialnetwork.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService; // ✅ Injected

    // send a friend request
    public Friendship sendRequest(Long requesterId, Long receiverId) {
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new RuntimeException("Requester not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        Friendship friendship = Friendship.builder()
                .requester(requester)
                .receiver(receiver)
                .status(FriendshipStatus.PENDING)
                .build();

        Friendship saved = friendshipRepository.save(friendship);

        // 🔔 Notify receiver
        notificationService.createNotification(
                receiverId,
                requester.getUsername() + " sent you a friend request"
        );

        return saved;
    }

    // accept friend request
    public Friendship acceptRequest(Long friendshipId) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new RuntimeException("Friendship not found"));

        friendship.setStatus(FriendshipStatus.ACCEPTED);
        return friendshipRepository.save(friendship);
    }

    // reject friend request
    public Friendship rejectRequest(Long friendshipId) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new RuntimeException("Friendship not found"));

        friendship.setStatus(FriendshipStatus.REJECTED);
        return friendshipRepository.save(friendship);
    }

    // block a user
    public Friendship blockUser(Long friendshipId) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new RuntimeException("Friendship not found"));

        friendship.setStatus(FriendshipStatus.BLOCKED);
        return friendshipRepository.save(friendship);
    }

    // list friends of a user
    public List<Friendship> listFriends(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return friendshipRepository.findAllByRequesterOrReceiverAndStatus(
                user, user, FriendshipStatus.ACCEPTED
        );
    }

    // list pending friend requests received by a user
    public List<Friendship> listPendingRequests(Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return friendshipRepository.findAllByReceiverAndStatus(user, FriendshipStatus.PENDING);
    }

    // list pending requests sent by a user
    public List<Friendship> listSentRequests(Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return friendshipRepository.findAllByRequesterAndStatus(user, FriendshipStatus.PENDING);
    }
}
