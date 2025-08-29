package com.youssef.socialnetwork.controllers;

import com.youssef.socialnetwork.dto.PostResponseDTO;
import com.youssef.socialnetwork.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/{authorId}")
    public ResponseEntity<PostResponseDTO> createPost(
            @PathVariable Long authorId,
            @RequestParam String content,
            @RequestParam(required = false) String mediaUrl) {
        return ResponseEntity.ok(postService.createPost(authorId, content, mediaUrl));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostResponseDTO>> getUserPosts(@PathVariable Long userId) {
        return ResponseEntity.ok(postService.getUserPosts(userId));
    }

    @GetMapping("/feed")
    public ResponseEntity<List<PostResponseDTO>> getAllPosts(@RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(postService.getAllPosts(page, size));
    }

    @DeleteMapping("/{postId}/{userId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId, @PathVariable Long userId) {
        postService.deletePost(postId, userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{postId}/{userId}")
    public ResponseEntity<PostResponseDTO> editPost(@PathVariable Long postId,
                                                    @PathVariable Long userId,
                                                    @RequestBody String newContent) {
        return ResponseEntity.ok(postService.editPost(postId, userId, newContent));
    }
}
