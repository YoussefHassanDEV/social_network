package com.youssef.socialnetwork.auth.dto;

import com.youssef.socialnetwork.Enums.VoteType;
import jakarta.validation.constraints.NotNull;

public record VoteRequest(@NotNull VoteType type) {}

