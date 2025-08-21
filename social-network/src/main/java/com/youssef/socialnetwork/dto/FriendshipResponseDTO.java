package com.youssef.socialnetwork.dto;

import com.youssef.socialnetwork.Enums.FriendshipStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendshipResponseDTO {
    private Long id;
    private Long requesterId;
    private String requesterName;
    private Long receiverId;
    private String receiverName;
    private FriendshipStatus status;
}
