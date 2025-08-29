package com.youssef.socialnetwork.model;

import com.youssef.socialnetwork.Enums.ReportCategory;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Post post;

    @ManyToOne
    private User reporter;

    @Enumerated(EnumType.STRING)
    private ReportCategory category;

    private LocalDateTime createdAt = LocalDateTime.now();
}
