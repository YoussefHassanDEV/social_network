package com.youssef.socialnetwork.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReportedPostDTO {
    private Long postId;
    private String authorName;
    private Integer reportsCount;
    private String content;
}
