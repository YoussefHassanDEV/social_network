package com.youssef.socialnetwork.repository;

import com.youssef.socialnetwork.model.Follow;
import com.youssef.socialnetwork.model.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    @Query("SELECT f.following FROM Follow f WHERE f.follower = :follower")
    List<User> findFollowingUsers(@Param("follower") User follower);

    List<Follow> findByFollower(User follower);
    List<Follow> findByFollowing(User following);
    boolean existsByFollowerAndFollowing(User follower, User following);
    void deleteByFollowerAndFollowing(User follower, User following);
}
