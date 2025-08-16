package com.youssef.socialnetwork.controllers;

import com.youssef.socialnetwork.model.Comment;
import com.youssef.socialnetwork.repository.CommentRepository;
import com.youssef.socialnetwork.repository.PostRepository;
import com.youssef.socialnetwork.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentRepository comments;
    private final PostRepository posts;
    private final UserRepository users;

    // Add comment
    @PostMapping("/{postId}")
    public ResponseEntity<?> addComment(@PathVariable Long postId, @RequestBody Comment comment, Principal principal) {
        var post = posts.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        var author = users.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        comment.setPost(post);
        comment.setAuthor(author);
        comment.setCreatedAt(Instant.now());

        comments.save(comment);

        return ResponseEntity.ok(comment);
    }

    // Get comments of a post
    @GetMapping("/{postId}")
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long postId) {
        var post = posts.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        return ResponseEntity.ok(comments.findAllByPost(post));
    }
}
