package com.youssef.socialnetwork.service;

import com.youssef.socialnetwork.dto.MessageResponseDTO;
import com.youssef.socialnetwork.model.Message;
import com.youssef.socialnetwork.model.User;
import com.youssef.socialnetwork.repository.MessageRepository;
import com.youssef.socialnetwork.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    private MessageResponseDTO toDto(Message msg) {
        return MessageResponseDTO.builder()
                .id(msg.getId())
                .senderId(msg.getSender().getId())
                .senderName(msg.getSender().getUsername())
                .receiverId(msg.getReceiver().getId())
                .receiverName(msg.getReceiver().getUsername())
                .content(msg.getContent())
                .timestamp(msg.getTimestamp())
                .read(msg.isRead())
                .build();
    }

    public MessageResponseDTO sendMessage(Long senderId, Long receiverId, String content) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        Message msg = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .content(content)
                .read(false)
                .build();

        return toDto(messageRepository.save(msg));
    }

    public List<MessageResponseDTO> getConversation(Long user1Id, Long user2Id, int page, int size) {
        User user1 = userRepository.findById(user1Id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User user2 = userRepository.findById(user2Id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return messageRepository
                .findBySenderAndReceiverOrReceiverAndSenderOrderByTimestampAsc(
                        user1, user2, user1, user2, PageRequest.of(page, size)
                )
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public void deleteMessage(Long messageId, Long userId) {
        Message msg = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        if (!msg.getSender().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this message");
        }

        messageRepository.delete(msg);
    }
}
