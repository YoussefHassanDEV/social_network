package com.youssef.socialnetwork.service;


import com.youssef.socialnetwork.Enums.VoteType;
import com.youssef.socialnetwork.model.Vote;
import com.youssef.socialnetwork.repository.PostRepository;
import com.youssef.socialnetwork.repository.UserRepository;
import com.youssef.socialnetwork.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository votes;
    private final PostRepository posts;
    private final UserRepository users;

    public VoteResult vote(String username, Long postId, VoteType requested) {
        var user = users.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        var post = posts.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        var existing = votes.findByUserAndPost(user, post);

        // Toggle rules:
        // - If same vote exists -> remove (neutral)
        // - If different or none -> upsert/update
        if (existing.isPresent()) {
            var v = existing.get();
            if (v.getType() == requested) {
                votes.delete(v); // toggle off
            } else {
                v.setType(requested);
                v.setCreatedAt(Instant.now());
                votes.save(v);
            }
        } else {
            var v = Vote.builder()
                    .user(user)
                    .post(post)
                    .type(requested)
                    .createdAt(Instant.now())
                    .build();
            votes.save(v);
        }

        long up = votes.countByPostAndType(post, VoteType.UPVOTE);
        long down = votes.countByPostAndType(post, VoteType.DOWNVOTE);
        var userVote = votes.findByUserAndPost(user, post).map(Vote::getType).orElse(null);

        return new VoteResult(up - down, userVote);
    }

    public VoteResult getScore(String username, Long postId) {
        var user = users.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        var post = posts.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        long up = votes.countByPostAndType(post, VoteType.UPVOTE);
        long down = votes.countByPostAndType(post, VoteType.DOWNVOTE);
        var userVote = votes.findByUserAndPost(user, post).map(Vote::getType).orElse(null);

        return new VoteResult(up - down, userVote);
    }

    public record VoteResult(long score, VoteType userVote) {}
}
