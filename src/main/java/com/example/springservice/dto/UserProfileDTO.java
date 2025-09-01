package com.example.springservice.dto;

import com.example.springservice.entites.enmap.FollowId;
import com.example.springservice.entites.User;
import com.example.springservice.repo.UserFollowsRepository;

public class UserProfileDTO {
    public Integer user_id;
    public String name;
    public String email;
    public String profile_picture;
    public String description;
    public String commission_status;
    public String role;
    public String status;
    public Boolean followedByMe; // ✅
    public Integer followerCount; // ✅

    public UserProfileDTO(User user) {
        this.user_id = user.getUserId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.profile_picture = user.getProfile_picture();
        this.description = user.getDescription();
        this.commission_status = user.getCommission_status().toString();
        this.role = user.getRole();
        this.status = user.getStatus().toString();
        this.followerCount = 0; // default
        this.followedByMe = false;
    }

    public UserProfileDTO(User user, Integer currentUserId, UserFollowsRepository followsRepo) {
        this(user);
        if (currentUserId != null && !user.getUserId().equals(currentUserId)) {
            FollowId id = new FollowId();
            id.setFollowerId(currentUserId);
            id.setFollowingId(user.getUserId());
            this.followedByMe = followsRepo.existsById(id);
        } else {
            this.followedByMe = false;
        }

        this.followerCount = followsRepo.findAllByFollowing_UserId(user.getUserId()).size();
    }
}


