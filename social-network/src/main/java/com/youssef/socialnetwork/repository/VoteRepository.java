package com.youssef.socialnetwork.repository;

import com.youssef.socialnetwork.Enums.VoteType;
import com.youssef.socialnetwork.model.Comment;
import com.youssef.socialnetwork.model.Post;
import com.youssef.socialnetwork.model.User;
import com.youssef.socialnetwork.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByUserAndPost(User user, Post post);
    Optional<Vote> findByUserAndComment(User user, Comment comment);

    long countByPostAndVoteType(Post post, VoteType voteType);
    long countByCommentAndVoteType(Comment comment, VoteType voteType);
}
