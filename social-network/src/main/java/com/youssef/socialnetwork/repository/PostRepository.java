package com.youssef.socialnetwork.repository;

import com.youssef.socialnetwork.model.Post;
import com.youssef.socialnetwork.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // === Counts ===
    long countByDeletedAtIsNull();
    long countByDeletedAtIsNotNull();

    @Query(value = "SELECT COUNT(*) FROM post WHERE CAST(created_at AS DATE) = CURRENT_DATE", nativeQuery = true)
    long countPostsCreatedToday();

    @Query(value = "SELECT COUNT(*) FROM post WHERE CAST(deleted_at AS DATE) = CURRENT_DATE", nativeQuery = true)
    long countPostsDeletedToday();

    // === Feeds ===
    List<Post> findAllByAuthor(User author);

    List<Post> findByAuthorIn(List<User> authors, Pageable pageable);

    @Query("""
        SELECT p FROM Post p
        WHERE (p.author IN :following OR p.language = :language OR p.country = :country)
          AND p.deletedAt IS NULL
        ORDER BY p.createdAt DESC
        """)
    List<Post> findSmartFeed(
            @Param("following") List<User> following,
            @Param("language") String language,
            @Param("country") String country,
            Pageable pageable
    );

    // === Trending ===
    @Query(value = """
        SELECT p.* 
        FROM post p
        LEFT JOIN votes v ON p.id = v.post_id
        WHERE p.deleted_at IS NULL
        GROUP BY p.id
        ORDER BY SUM(CASE WHEN v.type = 'UPVOTE' THEN 1 ELSE 0 END) -
                 SUM(CASE WHEN v.type = 'DOWNVOTE' THEN 1 ELSE 0 END) DESC,
                 p.created_at DESC
        """, nativeQuery = true)
    List<Post> findTrendingPosts(Pageable pageable);

    // === Admin Queries ===
    @Query("""
        SELECT p FROM Post p
        JOIN Report r ON r.post = p
        WHERE p.deletedAt IS NULL
        GROUP BY p
        ORDER BY COUNT(r) DESC
        """)
    List<Post> findMostReportedPosts(Pageable pageable);

    @Query("""
        SELECT p.author FROM Post p
        WHERE p.deletedAt IS NULL
        GROUP BY p.author
        ORDER BY COUNT(p) DESC
        """)
    List<User> findTopAuthors(Pageable pageable);
}
