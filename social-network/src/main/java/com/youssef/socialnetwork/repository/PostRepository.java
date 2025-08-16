package com.youssef.socialnetwork.repository;

import com.youssef.socialnetwork.model.Post;
import com.youssef.socialnetwork.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByAuthor(User author);
}
