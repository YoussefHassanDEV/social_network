package com.youssef.socialnetwork.service;

import com.youssef.socialnetwork.dto.PostResponseDTO;
import com.youssef.socialnetwork.model.Post;
import com.youssef.socialnetwork.model.User;
import com.youssef.socialnetwork.repository.PostRepository;
import com.youssef.socialnetwork.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final VoteService voteService;

    private PostResponseDTO toDto(Post post) {
        return PostResponseDTO.builder()
                .id(post.getId())
                .content(post.getContent())
                .authorName(post.getAuthor().getUsername())
                .upvotes(voteService.countPostUpvotes(post.getId()))
                .downvotes(voteService.countPostDownvotes(post.getId()))
                .build();
    }

    public List<PostResponseDTO> getAllPosts() {
        return postRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public PostResponseDTO createPost(Long authorId, String content) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Author not found"));

        Post post = Post.builder()
                .author(author)
                .content(content)
                .build();

        return toDto(postRepository.save(post));
    }

    public List<PostResponseDTO> getUserPosts(Long userId) {
        var author = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return postRepository.findAllByAuthor(author)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<PostResponseDTO> getAllPosts(int page, int size) {
        return postRepository.findAll(PageRequest.of(page, size))
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this post");
        }

        postRepository.delete(post);
    }

    public PostResponseDTO editPost(Long postId, Long userId, String newContent) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to edit this post");
        }

        post.setContent(newContent);
        return toDto(postRepository.save(post));
    }
}
