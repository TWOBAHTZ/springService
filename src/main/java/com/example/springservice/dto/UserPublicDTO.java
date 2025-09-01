package com.example.springservice.dto;

import com.example.springservice.entites.User;

public class UserPublicDTO {
    public Integer user_id;
    public String name;
    public String profile_picture;
    public String description;
    public String commission_status;

    public UserPublicDTO(User user) {
        this.user_id = user.getUserId();
        this.name = user.getName();
        this.profile_picture = user.getProfile_picture();
        this.description = user.getDescription();
        this.commission_status = user.getCommission_status().toString();
    }
}
