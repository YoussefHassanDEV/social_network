package com.youssef.socialnetwork.controllers;

import com.youssef.socialnetwork.model.User;
import com.youssef.socialnetwork.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping("/{followerId}/follow/{followingId}")
    public String followUser(@PathVariable Long followerId, @PathVariable Long followingId) {
        followService.followUser(followerId, followingId);
        return "Followed successfully";
    }

    @DeleteMapping("/{followerId}/unfollow/{followingId}")
    public String unfollowUser(@PathVariable Long followerId, @PathVariable Long followingId) {
        followService.unfollowUser(followerId, followingId);
        return "Unfollowed successfully";
    }

    @GetMapping("/{userId}/followers")
    public List<User> getFollowers(@PathVariable Long userId) {
        return followService.getFollowers(userId);
    }

    @GetMapping("/{userId}/following")
    public List<User> getFollowing(@PathVariable Long userId) {
        return followService.getFollowing(userId);
    }
}
