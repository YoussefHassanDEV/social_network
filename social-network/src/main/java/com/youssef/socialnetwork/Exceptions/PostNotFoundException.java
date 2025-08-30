package com.youssef.socialnetwork.Exceptions;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(Long postId) {
        super("Post with ID " + postId + " not found");
    }
}
