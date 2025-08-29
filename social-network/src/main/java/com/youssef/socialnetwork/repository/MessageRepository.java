package com.youssef.socialnetwork.repository;

import com.youssef.socialnetwork.model.Message;  // ✅ not Spring’s Message
import com.youssef.socialnetwork.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderAndReceiverOrReceiverAndSenderOrderByTimestampAsc(
            User sender1, User receiver1,
            User sender2, User receiver2,
            Pageable pageable
    );
}
