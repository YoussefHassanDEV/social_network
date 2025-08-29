package com.youssef.socialnetwork.service;

import com.youssef.socialnetwork.dto.UserProfileDTO;
import com.youssef.socialnetwork.model.User;
import com.youssef.socialnetwork.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;

    @Cacheable(value = "profiles", key = "#userId")
    public UserProfileDTO getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return toDto(user);
    }

    @CacheEvict(value = "profiles", key = "#userId")
    public UserProfileDTO updateProfile(Long userId, UserProfileDTO profileData) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setBio(profileData.getBio());
        user.setProfilePictureUrl(profileData.getProfilePictureUrl());
        user.setCoverPhotoUrl(profileData.getCoverPhotoUrl());
        user.setLocation(profileData.getLocation());
        user.setJobTitle(profileData.getJobTitle());

        return toDto(userRepository.save(user));
    }

    private UserProfileDTO toDto(User user) {
        return UserProfileDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .bio(user.getBio())
                .profilePictureUrl(user.getProfilePictureUrl())
                .coverPhotoUrl(user.getCoverPhotoUrl())
                .location(user.getLocation())
                .jobTitle(user.getJobTitle())
                .build();
    }
}
