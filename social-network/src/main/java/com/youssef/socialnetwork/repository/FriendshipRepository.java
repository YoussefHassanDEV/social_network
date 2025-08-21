package com.youssef.socialnetwork.repository;

import com.youssef.socialnetwork.Enums.FriendshipStatus;
import com.youssef.socialnetwork.model.Friendship;
import com.youssef.socialnetwork.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    Optional<Friendship> findByRequesterAndReceiver(User requester, User receiver);

    List<Friendship> findAllByRequesterAndStatus(User requester, FriendshipStatus status);

    List<Friendship> findAllByReceiverAndStatus(User receiver, FriendshipStatus status);
    List<Friendship> findAllByRequesterOrReceiverAndStatus(User requester, User receiver, FriendshipStatus status);

}
