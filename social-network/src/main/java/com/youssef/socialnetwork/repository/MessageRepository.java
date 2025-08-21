package com.youssef.socialnetwork.repository;

import com.youssef.socialnetwork.model.Message;
import com.youssef.socialnetwork.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {

    Page<Message> findBySenderAndReceiverOrReceiverAndSenderOrderByTimestampAsc(
            User sender1, User receiver1, User sender2, User receiver2, Pageable pageable
    );
}
