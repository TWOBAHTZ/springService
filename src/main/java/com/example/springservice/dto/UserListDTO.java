package com.example.springservice.dto;

import com.example.springservice.entites.User;

public class UserListDTO {
    public Integer user_id;
    public String name;
    public String email;
    public String role;
    public String status;

    public UserListDTO(User user) {
        this.user_id = user.getUserId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.status = user.getStatus().toString();
    }
}
