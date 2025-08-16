package com.youssef.socialnetwork.controllers;

import com.youssef.socialnetwork.model.Post;
import com.youssef.socialnetwork.repository.PostRepository;
import com.youssef.socialnetwork.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostRepository posts;
    private final UserRepository users;

    // Create new post
    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody Post post, Principal principal) {
        var author = users.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        post.setAuthor(author);
        post.setCreatedAt(Instant.now());
        posts.save(post);

        return ResponseEntity.ok(post);
    }

    // Get all posts
    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        return ResponseEntity.ok(posts.findAll());
    }

    // Get posts by current user
    @GetMapping("/me")
    public ResponseEntity<List<Post>> getMyPosts(Principal principal) {
        var me = users.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return ResponseEntity.ok(posts.findAllByAuthor(me));
    }

    // Delete post
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id, Principal principal) {
        var post = posts.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        if (!post.getAuthor().getUsername().equals(principal.getName())) {
            return ResponseEntity.status(403).body("You cannot delete this post");
        }

        posts.delete(post);
        return ResponseEntity.ok("Post deleted");
    }
}
