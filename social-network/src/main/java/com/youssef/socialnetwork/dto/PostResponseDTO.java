package com.youssef.socialnetwork.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponseDTO {
    private Long id;
    private String content;
    private String authorName;
    private long upvotes;
    private long downvotes;
    private String mediaUrl;
}
