package com.youssef.socialnetwork.service;

import com.youssef.socialnetwork.Enums.VoteType;
import com.youssef.socialnetwork.dto.VoteResponse;
import com.youssef.socialnetwork.model.Comment;
import com.youssef.socialnetwork.model.Post;
import com.youssef.socialnetwork.model.User;
import com.youssef.socialnetwork.model.Vote;
import com.youssef.socialnetwork.repository.CommentRepository;
import com.youssef.socialnetwork.repository.PostRepository;
import com.youssef.socialnetwork.repository.UserRepository;
import com.youssef.socialnetwork.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final NotificationService notificationService; // ✅ inject notifications

    // Vote on Post
    public VoteResponse votePost(Long postId, Long userId, VoteType voteType) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        voteRepository.findByUserAndPost(user, post).ifPresentOrElse(existingVote -> {
            if (existingVote.getVoteType() == voteType) {
                voteRepository.delete(existingVote); // remove vote
            } else {
                existingVote.setVoteType(voteType);  // change vote
                voteRepository.save(existingVote);
            }
        }, () -> {
            Vote vote = Vote.builder()
                    .user(user)
                    .post(post)
                    .voteType(voteType)
                    .build();
            voteRepository.save(vote);
        });

        // 🔔 notify post author if someone else votes
        if (!user.getId().equals(post.getAuthor().getId())) {
            notificationService.createNotification(
                    post.getAuthor().getId(),
                    user.getUsername() + " " + voteType.name().toLowerCase() + "d your post"
            );
        }

        long score = voteRepository.countByPostAndVoteType(post, VoteType.UPVOTE)
                - voteRepository.countByPostAndVoteType(post, VoteType.DOWNVOTE);

        return new VoteResponse(score, voteType);
    }

    // Vote on Comment
    public VoteResponse voteComment(Long commentId, Long userId, VoteType voteType) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        voteRepository.findByUserAndComment(user, comment).ifPresentOrElse(existingVote -> {
            if (existingVote.getVoteType() == voteType) {
                voteRepository.delete(existingVote);
            } else {
                existingVote.setVoteType(voteType);
                voteRepository.save(existingVote);
            }
        }, () -> {
            Vote vote = Vote.builder()
                    .user(user)
                    .comment(comment)
                    .voteType(voteType)
                    .build();
            voteRepository.save(vote);
        });

        // 🔔 notify comment author if someone else votes
        if (!user.getId().equals(comment.getAuthor().getId())) {
            notificationService.createNotification(
                    comment.getAuthor().getId(),
                    user.getUsername() + " " + voteType.name().toLowerCase() + "d your comment"
            );
        }

        long score = voteRepository.countByCommentAndVoteType(comment, VoteType.UPVOTE)
                - voteRepository.countByCommentAndVoteType(comment, VoteType.DOWNVOTE);

        return new VoteResponse(score, voteType);
    }

    // count helpers
    public long countPostUpvotes(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return voteRepository.countByPostAndVoteType(post, VoteType.UPVOTE);
    }

    public long countPostDownvotes(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return voteRepository.countByPostAndVoteType(post, VoteType.DOWNVOTE);
    }

    public long countCommentUpvotes(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        return voteRepository.countByCommentAndVoteType(comment, VoteType.UPVOTE);
    }

    public long countCommentDownvotes(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        return voteRepository.countByCommentAndVoteType(comment, VoteType.DOWNVOTE);
    }
}
