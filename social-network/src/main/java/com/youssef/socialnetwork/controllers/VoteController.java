package com.youssef.socialnetwork.controllers;

import com.youssef.socialnetwork.auth.dto.VoteRequest;
import com.youssef.socialnetwork.auth.dto.VoteResponse;
import com.youssef.socialnetwork.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    // Vote or toggle (UPVOTE/DOWNVOTE)
    @PostMapping("/{postId}")
    public ResponseEntity<VoteResponse> vote(
            Principal principal,
            @PathVariable Long postId,
            @RequestBody @Valid VoteRequest req) {

        var result = voteService.vote(principal.getName(), postId, req.type());
        return ResponseEntity.ok(new VoteResponse(result.score(), result.userVote()));
    }

    // Get score + user's current vote
    @GetMapping("/{postId}")
    public ResponseEntity<VoteResponse> get(
            Principal principal,
            @PathVariable Long postId) {

        var result = voteService.getScore(principal.getName(), postId);
        return ResponseEntity.ok(new VoteResponse(result.score(), result.userVote()));
    }
}
