package com.youssef.socialnetwork.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PagedResponse<T> {
    private int page;
    private int size;
    private List<T> data;
}
