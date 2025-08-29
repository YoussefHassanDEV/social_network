package com.youssef.socialnetwork.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "follows")
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // الـ User اللي بيعمل Follow
    @ManyToOne
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;

    // الـ User اللي بيتعمله Follow
    @ManyToOne
    @JoinColumn(name = "following_id", nullable = false)
    private User following;
}
