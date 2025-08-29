package com.youssef.socialnetwork.repository;

import com.youssef.socialnetwork.model.ChatGroup;
import com.youssef.socialnetwork.model.GroupMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupMessageRepository extends JpaRepository<GroupMessage, Long> {
    List<GroupMessage> findByGroupOrderByTimestampAsc(ChatGroup group);
}
