package com.youssef.socialnetwork.controllers;

import com.youssef.socialnetwork.dto.UserProfileDTO;
import com.youssef.socialnetwork.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileDTO> getProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(profileService.getProfile(userId));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserProfileDTO> updateProfile(
            @PathVariable Long userId,
            @RequestBody UserProfileDTO profileData) {
        return ResponseEntity.ok(profileService.updateProfile(userId, profileData));
    }
}