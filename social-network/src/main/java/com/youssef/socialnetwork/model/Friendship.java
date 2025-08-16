package com.youssef.socialnetwork.model;

import com.youssef.socialnetwork.Enums.FriendshipStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User requester; // who sent the request

    @ManyToOne
    @JoinColumn(nullable = false)
    private User receiver;  // who received the request

    @Enumerated(EnumType.STRING)
    private FriendshipStatus status; // PENDING, ACCEPTED, BLOCKED
}
