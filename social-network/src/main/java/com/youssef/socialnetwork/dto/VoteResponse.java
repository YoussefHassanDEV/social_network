package com.youssef.socialnetwork.dto;

import com.youssef.socialnetwork.Enums.VoteType;

public record VoteResponse(long score, VoteType userVote) {}
