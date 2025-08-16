package com.youssef.socialnetwork.repository;

import com.youssef.socialnetwork.Enums.VoteType;
import com.youssef.socialnetwork.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByUserAndPost(User user, Post post);
    long countByPostAndType(Post post, VoteType type);
    void deleteByUserAndPost(User user, Post post);
}
