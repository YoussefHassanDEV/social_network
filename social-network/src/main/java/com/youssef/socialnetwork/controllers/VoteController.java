package com.youssef.socialnetwork.controllers;

import com.youssef.socialnetwork.Enums.VoteType;
import com.youssef.socialnetwork.dto.VoteResponse;
import com.youssef.socialnetwork.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @PostMapping("/post/{postId}/user/{userId}")
    public ResponseEntity<VoteResponse> votePost(@PathVariable Long postId,
                                                 @PathVariable Long userId,
                                                 @RequestParam VoteType voteType) {
        return ResponseEntity.ok(voteService.votePost(postId, userId, voteType));
    }

    @PostMapping("/comment/{commentId}/user/{userId}")
    public ResponseEntity<VoteResponse> voteComment(@PathVariable Long commentId,
                                                    @PathVariable Long userId,
                                                    @RequestParam VoteType voteType) {
        return ResponseEntity.ok(voteService.voteComment(commentId, userId, voteType));
    }

    @GetMapping("/post/{postId}/upvotes")
    public ResponseEntity<Long> countPostUpvotes(@PathVariable Long postId) {
        return ResponseEntity.ok(voteService.countPostUpvotes(postId));
    }

    @GetMapping("/post/{postId}/downvotes")
    public ResponseEntity<Long> countPostDownvotes(@PathVariable Long postId) {
        return ResponseEntity.ok(voteService.countPostDownvotes(postId));
    }

    @GetMapping("/comment/{commentId}/upvotes")
    public ResponseEntity<Long> countCommentUpvotes(@PathVariable Long commentId) {
        return ResponseEntity.ok(voteService.countCommentUpvotes(commentId));
    }

    @GetMapping("/comment/{commentId}/downvotes")
    public ResponseEntity<Long> countCommentDownvotes(@PathVariable Long commentId) {
        return ResponseEntity.ok(voteService.countCommentDownvotes(commentId));
    }
}
