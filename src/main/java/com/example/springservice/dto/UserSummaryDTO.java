package com.example.springservice.dto;

import com.example.springservice.entites.User;

public class UserSummaryDTO {
    public Integer id;
    public String name;
    public String profilePicture;

    public UserSummaryDTO(User user) {
        this.id = user.getUserId();
        this.name = user.getName();
        this.profilePicture = user.getProfilePicture(); // getter ที่นายเขียนไว้แล้ว
    }
}
