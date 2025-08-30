package com.youssef.socialnetwork.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TopAuthorDTO {
    private Long authorId;
    private String authorName;
    private Integer postsCount;
}
