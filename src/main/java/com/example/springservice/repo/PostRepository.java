package com.example.springservice.repo;

import com.example.springservice.entites.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {
    List<Post> findAllByAuthor_UserIdOrderByCreatedAtDesc(Integer userId);
    List<Post> findAllByAuthor_UserIdInOrderByCreatedAtDesc(List<Integer> userIds);
}