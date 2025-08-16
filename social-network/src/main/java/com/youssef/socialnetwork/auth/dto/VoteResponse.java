package com.youssef.socialnetwork.auth.dto;

import com.youssef.socialnetwork.Enums.VoteType;

public record VoteResponse(long score, VoteType userVote) {}
