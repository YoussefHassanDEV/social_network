package com.youssef.socialnetwork.controllers;

import com.youssef.socialnetwork.dto.CommentResponseDTO;
import com.youssef.socialnetwork.model.Comment;
import com.youssef.socialnetwork.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{postId}/{userId}")
    public ResponseEntity<Comment> addComment(@PathVariable Long postId,
                                              @PathVariable Long userId,
                                              @RequestBody String content) {
        return ResponseEntity.ok(commentService.addComment(postId, userId, content));
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentResponseDTO>> getCommentsForPost(@PathVariable Long postId,
                                                                       @RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(commentService.getCommentsForPost(postId, page, size));
    }

    @DeleteMapping("/{commentId}/{userId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId,
                                              @PathVariable Long userId) {
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{commentId}/{userId}")
    public ResponseEntity<Comment> editComment(@PathVariable Long commentId,
                                               @PathVariable Long userId,
                                               @RequestBody String newContent) {
        return ResponseEntity.ok(commentService.editComment(commentId, userId, newContent));
    }
}
