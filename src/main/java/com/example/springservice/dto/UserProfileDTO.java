package com.example.springservice.dto;

import com.example.springservice.entites.User;

public class UserProfileDTO {
    public Integer user_id;
    public String name;
    public String email;
    public String profile_picture;
    public String description;
    public String commission_status;
    public String role;
    public String status;

    public UserProfileDTO(User user) {
        this.user_id = user.getUserId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.profile_picture = user.getProfile_picture();
        this.description = user.getDescription();
        this.commission_status = user.getCommission_status().toString();
        this.role = user.getRole();
        this.status = user.getStatus().toString();
    }
}
