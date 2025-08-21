package com.youssef.socialnetwork.model;

import com.youssef.socialnetwork.Enums.VoteType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne
    private Post post;

    @ManyToOne
    private Comment comment;

    @Enumerated(EnumType.STRING)
    private VoteType voteType;
}
