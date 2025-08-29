package com.youssef.socialnetwork.service;

import com.youssef.socialnetwork.Enums.ReportCategory;
import com.youssef.socialnetwork.dto.PostResponseDTO;
import com.youssef.socialnetwork.model.Follow;
import com.youssef.socialnetwork.model.Post;
import com.youssef.socialnetwork.model.User;
import com.youssef.socialnetwork.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final VoteService voteService;
    private final ReportRepository reportRepository;
    private final FollowRepository followRepository;

    private PostResponseDTO toDto(Post post, User viewer) {
        if (post.isBlurred()) {
            if (viewer.getAge() < 21) {
                return null; // 🚫 ممنوع على الأقل من 21
            }
            return PostResponseDTO.builder()
                    .id(post.getId())
                    .content("⚠️ " + post.getBlurReason())
                    .authorName(post.getAuthor().getUsername())
                    .upvotes(voteService.countPostUpvotes(post.getId()))
                    .downvotes(voteService.countPostDownvotes(post.getId()))
                    .mediaUrl(null)
                    .blurred(true)
                    .blurReason(post.getBlurReason())
                    .build();
        }
        return PostResponseDTO.builder()
                .id(post.getId())
                .content(post.getContent())
                .authorName(post.getAuthor().getUsername())
                .upvotes(voteService.countPostUpvotes(post.getId()))
                .downvotes(voteService.countPostDownvotes(post.getId()))
                .mediaUrl(post.getMediaUrl())
                .blurred(false)
                .build();
    }

    @Cacheable(value = "allPosts")
    public List<PostResponseDTO> getAllPosts(Long viewerId) {
        User viewer = userRepository.findById(viewerId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return postRepository.findAll().stream()
                .map(p -> toDto(p, viewer))
                .filter(dto -> dto != null)
                .toList();
    }

    @Cacheable(value = "posts", key = "#page + '-' + #size + '-' + #viewerId")
    public List<PostResponseDTO> getAllPosts(int page, int size, Long viewerId) {
        User viewer = userRepository.findById(viewerId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return postRepository.findAll(PageRequest.of(page, size)).stream()
                .map(p -> toDto(p, viewer))
                .filter(dto -> dto != null)
                .toList();
    }

    @Cacheable(value = "userPosts", key = "#authorId + '-' + #viewerId")
    public List<PostResponseDTO> getUserPosts(Long authorId, Long viewerId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Author not found"));
        User viewer = userRepository.findById(viewerId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return postRepository.findAllByAuthor(author).stream()
                .map(p -> toDto(p, viewer))
                .filter(dto -> dto != null)
                .toList();
    }

    @CacheEvict(value = {"allPosts", "userPosts"}, allEntries = true)
    public PostResponseDTO createPost(Long authorId, String content, String mediaUrl) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Author not found"));
        Post post = Post.builder()
                .author(author)
                .content(content)
                .mediaUrl(mediaUrl)
                .build();
        return toDto(postRepository.save(post), author);
    }

    @CacheEvict(value = {"allPosts", "userPosts"}, allEntries = true)
    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        if (!post.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        postRepository.delete(post);
    }

    @CacheEvict(value = {"allPosts", "userPosts"}, allEntries = true)
    public PostResponseDTO editPost(Long postId, Long userId, String newContent) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        if (!post.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        post.setContent(newContent);
        return toDto(postRepository.save(post), post.getAuthor());
    }

    public void checkModeration(Post post) {
        long racismReports = reportRepository.countByPostAndCategory(post, ReportCategory.RACISM);
        long nudityReports = reportRepository.countByPostAndCategory(post, ReportCategory.NUDITY);
        long violenceReports = reportRepository.countByPostAndCategory(post, ReportCategory.VIOLENCE);

        if (racismReports > 100) {
            post.setBlurred(true);
            post.setBlurReason("⚠️ Potential Racism");
        } else if (nudityReports > 100) {
            post.setBlurred(true);
            post.setBlurReason("⚠️ Potential Nudity");
        } else if (violenceReports > 100) {
            post.setBlurred(true);
            post.setBlurReason("⚠️ Potential Violence");
        }
        postRepository.save(post);
    }

    public List<PostResponseDTO> getFollowersPosts(Long userId, int page, int size, Long viewerId) {
        User me = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User viewer = userRepository.findById(viewerId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<User> followers = followRepository.findByFollowing(me).stream()
                .map(Follow::getFollower).toList();

        return postRepository.findByAuthorIn(followers, PageRequest.of(page, size)).stream()
                .map(p -> toDto(p, viewer))
                .filter(dto -> dto != null)
                .toList();
    }

    public List<PostResponseDTO> getFollowingPosts(Long userId, int page, int size, Long viewerId) {
        User me = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User viewer = userRepository.findById(viewerId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<User> following = followRepository.findByFollower(me).stream()
                .map(Follow::getFollowing).toList();

        return postRepository.findByAuthorIn(following, PageRequest.of(page, size)).stream()
                .map(p -> toDto(p, viewer))
                .filter(dto -> dto != null)
                .toList();
    }

    public List<PostResponseDTO> getTrendingPosts(int page, int size, Long viewerId) {
        User viewer = userRepository.findById(viewerId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return postRepository.findTrendingPosts(PageRequest.of(page, size)).stream()
                .map(p -> toDto(p, viewer))
                .filter(dto -> dto != null)
                .toList();
    }

    public List<PostResponseDTO> getRecommendedPosts(Long userId, int page, int size, Long viewerId) {
        User me = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User viewer = userRepository.findById(viewerId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<User> following = followRepository.findFollowingUsers(me);

        return postRepository.findSmartFeed(
                        following,
                        me.getLanguage(),
                        me.getCountry(),
                        PageRequest.of(page, size)
                ).stream()
                .map(p -> toDto(p, viewer))
                .filter(dto -> dto != null)
                .toList();
    }
}
