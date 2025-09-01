package com.example.springservice.controller;

import com.example.springservice.dto.*;
import com.example.springservice.dto.UserPublicDTO;
import com.example.springservice.entites.Post;
import com.example.springservice.entites.User;
import com.example.springservice.repo.PostRepository;
import com.example.springservice.repo.UserFollowsRepository;
import com.example.springservice.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

// ✅ GuestController.java
@RestController
@RequestMapping("/guest")
public class GuestController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userRepository.findAllSortedByName();
        List<UserPublicDTO> response = users.stream().map(UserPublicDTO::new).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/search")
    public ResponseEntity<?> searchUsersByName(@RequestParam String name) {
        List<User> users = userRepository.searchByNameSorted(name);
        List<UserPublicDTO> response = users.stream().map(UserPublicDTO::new).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/posts")
    public ResponseEntity<?> getAllPosts() {
        List<Post> posts = postRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        List<PostResponseDTO> response = posts.stream()
                .map(post -> {
                    User author = post.getAuthor();
                    List<String> styles = author.getStyleMappings().stream()
                            .map(m -> m.getStyle().getStyleName())
                            .toList();

                    PostResponseDTO dto = new PostResponseDTO(post, null);
                    dto.styles = styles;
                    dto.guestComments = post.getComments().stream()
                            .map(CommentGuestDTO::new)
                            .toList();
                    return dto;
                })
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<?> getPostById(@PathVariable Integer id) {
        Optional<Post> postOpt = postRepository.findById(id);
        if (postOpt.isEmpty()) return ResponseEntity.status(404).body(Map.of("error", "Post not found"));

        Post post = postOpt.get();
        User author = post.getAuthor();
        List<String> styles = author.getStyleMappings().stream()
                .map(m -> m.getStyle().getStyleName())
                .toList();

        PostResponseDTO dto = new PostResponseDTO(post, null);
        dto.styles = styles;
        dto.guestComments = post.getComments().stream()
                .map(CommentGuestDTO::new)
                .toList();

        return ResponseEntity.ok(dto);
    }


}

