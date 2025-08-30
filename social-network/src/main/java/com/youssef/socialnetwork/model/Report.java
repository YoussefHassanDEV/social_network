package com.youssef.socialnetwork.model;

import com.youssef.socialnetwork.Enums.ReportCategory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Getter
@Setter
@NoArgsConstructor
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ Post اللي عليه التقرير
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    // ✅ User اللي عمل التقرير
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    // ✅ نوع التقرير (Spam, Violence, etc)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportCategory category;

    private LocalDateTime createdAt = LocalDateTime.now();
}
