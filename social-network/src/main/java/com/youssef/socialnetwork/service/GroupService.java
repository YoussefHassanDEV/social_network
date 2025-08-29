package com.youssef.socialnetwork.service;

import com.youssef.socialnetwork.dto.GroupMessageDTO;
import com.youssef.socialnetwork.model.ChatGroup;
import com.youssef.socialnetwork.model.GroupMessage;
import com.youssef.socialnetwork.model.User;
import com.youssef.socialnetwork.repository.ChatGroupRepository;
import com.youssef.socialnetwork.repository.GroupMessageRepository;
import com.youssef.socialnetwork.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final ChatGroupRepository groupRepo;
    private final GroupMessageRepository msgRepo;
    private final UserRepository userRepo;

    public ChatGroup createGroup(String name, List<Long> memberIds) {
        List<User> members = userRepo.findAllById(memberIds);
        ChatGroup group = ChatGroup.builder().name(name).members(members).build();
        return groupRepo.save(group);
    }

    public void addMember(Long groupId, Long userId) {
        ChatGroup group = groupRepo.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        group.getMembers().add(user);
        groupRepo.save(group);
    }

    public GroupMessageDTO sendMessage(Long groupId, Long senderId, String content) {
        ChatGroup group = groupRepo.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        User sender = userRepo.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        if (!group.getMembers().contains(sender))
            throw new RuntimeException("User not in group");

        GroupMessage msg = msgRepo.save(
                GroupMessage.builder().group(group).sender(sender).content(content).build()
        );

        return toDto(msg);
    }

    public List<GroupMessageDTO> getMessages(Long groupId) {
        ChatGroup group = groupRepo.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        return msgRepo.findByGroupOrderByTimestampAsc(group)
                .stream().map(this::toDto).toList();
    }

    private GroupMessageDTO toDto(GroupMessage msg) {
        return GroupMessageDTO.builder()
                .id(msg.getId())
                .senderId(msg.getSender().getId())
                .senderName(msg.getSender().getUsername())
                .content(msg.getContent())
                .timestamp(msg.getTimestamp())
                .build();
    }
}
