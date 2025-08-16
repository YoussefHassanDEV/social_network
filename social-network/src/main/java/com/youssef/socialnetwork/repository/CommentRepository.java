package com.youssef.socialnetwork.repository;

import com.youssef.socialnetwork.model.Comment;
import com.youssef.socialnetwork.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPost(Post post);
}
