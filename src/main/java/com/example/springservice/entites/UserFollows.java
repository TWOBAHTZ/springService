package com.example.springservice.entites;

import com.example.springservice.entites.enmap.FollowId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Setter
@Getter
@Entity

@Table(name = "user_follows")
public class UserFollows {
    @EmbeddedId
    private FollowId id = new FollowId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("followerId")
    @JoinColumn(name = "follower_user_id", nullable = false)
    private User follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("followingId")
    @JoinColumn(name = "following_user_id", nullable = false)
    private User following;


    @Column(name = "follow_date", nullable = false)
    private LocalDateTime followDate = LocalDateTime.now();

    public void setFollower(User follower) {
        this.follower = follower;
        this.id.setFollowerId(follower.getUserId());
    }

    public void setFollowing(User following) {
        this.following = following;
        this.id.setFollowingId(following.getUserId());
    }
}

