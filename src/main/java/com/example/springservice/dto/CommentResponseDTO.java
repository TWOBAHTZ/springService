package com.example.springservice.dto;

import com.example.springservice.entites.Comment;

public class CommentResponseDTO {
    public String content;
    public String createdAt;
    public Integer userId;
    public String name;
    public String profilePicture;

    public CommentResponseDTO(Comment comment) {
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt().toString();
        this.userId = comment.getAuthor().getUserId();
        this.name = comment.getAuthor().getName();
        this.profilePicture = comment.getAuthor().getProfile_picture();
    }
}

