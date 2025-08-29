package com.youssef.socialnetwork.service;

import com.youssef.socialnetwork.model.Follow;
import com.youssef.socialnetwork.model.User;
import com.youssef.socialnetwork.repository.FollowRepository;
import com.youssef.socialnetwork.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepo;
    private final UserRepository userRepo;

    public void followUser(Long followerId, Long followingId) {
        User follower = userRepo.findById(followerId)
                .orElseThrow(() -> new RuntimeException("Follower not found"));
        User following = userRepo.findById(followingId)
                .orElseThrow(() -> new RuntimeException("User to follow not found"));

        if (followRepo.existsByFollowerAndFollowing(follower, following)) {
            throw new RuntimeException("Already following this user");
        }

        Follow follow = Follow.builder()
                .follower(follower)
                .following(following)
                .build();
        followRepo.save(follow);
    }

    public void unfollowUser(Long followerId, Long followingId) {
        User follower = userRepo.findById(followerId)
                .orElseThrow(() -> new RuntimeException("Follower not found"));
        User following = userRepo.findById(followingId)
                .orElseThrow(() -> new RuntimeException("User to unfollow not found"));

        followRepo.deleteByFollowerAndFollowing(follower, following);
    }

    public List<User> getFollowers(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return followRepo.findByFollowing(user).stream()
                .map(Follow::getFollower)
                .toList();
    }

    public List<User> getFollowing(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return followRepo.findByFollower(user).stream()
                .map(Follow::getFollowing)
                .toList();
    }
}
