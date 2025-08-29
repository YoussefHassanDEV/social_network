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
    long countByDeletedAtIsNull();
    long countByDeletedAtIsNotNull();

    @Query("SELECT COUNT(p) FROM Post p WHERE DATE(p.createdAt) = CURRENT_DATE")
    long countPostsCreatedToday();

    @Query("SELECT COUNT(p) FROM Post p WHERE DATE(p.deletedAt) = CURRENT_DATE")
    long countPostsDeletedToday();
    // ✅ بوستات يوزر واحد
    List<Post> findAllByAuthor(User author);

    // ✅ بوستات مجموعة يوزرز (followers / following)
    List<Post> findByAuthorIn(List<User> authors, Pageable pageable);

    // ✅ Smart Feed (لغة + بلد + followings)
    @Query("""
        SELECT p FROM Post p
        WHERE p.author IN :following
           OR p.language = :language
           OR p.country = :country
        ORDER BY p.createdAt DESC
        """)
    List<Post> findSmartFeed(
            @Param("following") List<User> following,
            @Param("language") String language,
            @Param("country") String country,
            Pageable pageable
    );

    // ✅ Trending posts (native query using votes table)
    @Query(value = """
        SELECT p.* 
        FROM posts p
        LEFT JOIN votes v ON p.id = v.post_id
        GROUP BY p.id
        ORDER BY SUM(CASE WHEN v.type = 'UPVOTE' THEN 1 ELSE 0 END) -
                 SUM(CASE WHEN v.type = 'DOWNVOTE' THEN 1 ELSE 0 END) DESC,
                 p.created_at DESC
        """, nativeQuery = true)
    List<Post> findTrendingPosts(Pageable pageable);
    @Query("""
    SELECT p FROM Post p
    JOIN Report r ON r.post = p
    GROUP BY p
    ORDER BY COUNT(r) DESC
    """)
    List<Post> findMostReportedPosts(Pageable pageable);
    @Query("""
    SELECT p.author FROM Post p
    GROUP BY p.author
    ORDER BY COUNT(p) DESC
    """)
    List<User> findTopAuthors(Pageable pageable);

}
