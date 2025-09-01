package com.example.springservice.dto;

import com.example.springservice.entites.Comment;

public class CommentGuestDTO {
    public String content;
    public String createdAt;

    public CommentGuestDTO(Comment comment) {
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt().toString();
    }
}
