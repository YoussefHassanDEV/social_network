package com.youssef.socialnetwork.service;

import com.youssef.socialnetwork.dto.CommentResponseDTO;
import com.youssef.socialnetwork.model.Comment;
import com.youssef.socialnetwork.model.Post;
import com.youssef.socialnetwork.model.User;
import com.youssef.socialnetwork.repository.CommentRepository;
import com.youssef.socialnetwork.repository.PostRepository;
import com.youssef.socialnetwork.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final VoteService voteService;

    public Comment addComment(Long postId, Long userId, String content) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Comment comment = Comment.builder().post(post).author(user).content(content).build();
        return commentRepository.save(comment);
    }

    public List<CommentResponseDTO> getCommentsForPost(Long postId, int page, int size) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        return commentRepository.findAllByPost(post, PageRequest.of(page, size))
                .stream()
                .map(comment -> CommentResponseDTO.builder()
                        .id(comment.getId())
                        .content(comment.getContent())
                        .authorName(comment.getAuthor().getUsername())
                        .upvotes(voteService.countCommentUpvotes(comment.getId()))
                        .downvotes(voteService.countCommentDownvotes(comment.getId()))
                        .build())
                .collect(Collectors.toList());
    }

    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this comment");
        }
        commentRepository.delete(comment);
    }

    public Comment editComment(Long commentId, Long userId, String newContent) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to edit this comment");
        }
        comment.setContent(newContent);
        return commentRepository.save(comment);
    }
}
