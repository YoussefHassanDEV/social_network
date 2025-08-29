package com.youssef.socialnetwork.repository;

import com.youssef.socialnetwork.model.Message;
import com.youssef.socialnetwork.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("""
        SELECT m FROM Message m
        WHERE (m.sender = :u1 AND m.receiver = :u2)
           OR (m.sender = :u2 AND m.receiver = :u1)
        ORDER BY m.timestamp ASC
    """)
    List<Message> findConversation(@Param("u1") User u1,
                                   @Param("u2") User u2,
                                   Pageable pageable);
}
