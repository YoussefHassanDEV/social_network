package com.youssef.socialnetwork.repository;

import com.youssef.socialnetwork.model.ChatGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatGroupRepository extends JpaRepository<ChatGroup, Long> {
}
