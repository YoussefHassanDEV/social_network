package com.youssef.socialnetwork.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupMessageDTO {
    private Long id;
    private Long senderId;
    private String senderName;
    private String content;
    private LocalDateTime timestamp;
}