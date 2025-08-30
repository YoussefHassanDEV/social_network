package com.youssef.socialnetwork.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User author;

    @Column(nullable = false, length = 1000)
    private String content;

    private String mediaUrl;   // Video or picture

    private String language;
    private String country;

    private boolean blurred = false;

    private String blurReason; // e.g., "Potential Racism", "Potential Nudity"
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> reports = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
