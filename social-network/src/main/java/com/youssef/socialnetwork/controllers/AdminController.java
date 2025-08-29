package com.youssef.socialnetwork.controllers;

import com.youssef.socialnetwork.model.Report;
import com.youssef.socialnetwork.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @PostMapping("/ban-user/{userId}")
    public ResponseEntity<Void> banUser(@PathVariable Long userId) {
        adminService.banUser(userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete-user/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete-post/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        adminService.deletePost(postId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user-stats")
    public ResponseEntity<Map<String, Long>> getUserStats() {
        return ResponseEntity.ok(adminService.getUserStats());
    }

    @GetMapping("/post-stats")
    public ResponseEntity<Map<String, Long>> getPostStats() {
        return ResponseEntity.ok(adminService.getPostStats());
    }
}
