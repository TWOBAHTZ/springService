package com.example.springservice.repo;

import com.example.springservice.entites.User;
import com.example.springservice.entites.UserFollows;
import com.example.springservice.entites.enmap.FollowId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserFollowsRepository extends JpaRepository<UserFollows, FollowId> {
    List<UserFollows> findAllByFollower_UserId(Integer followerId);
    List<UserFollows> findAllByFollowing_UserId(Integer followingId);

}
