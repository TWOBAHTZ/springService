package com.example.springservice.controller;

import com.example.springservice.SessionUtil;
import com.example.springservice.dto.PostCreateDTO;
import com.example.springservice.dto.PostResponseDTO;
import com.example.springservice.entites.*;
import com.example.springservice.repo.PostRepository;
import com.example.springservice.repo.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    private ResponseEntity<?> validateUser(HttpServletRequest request) {
        //✅ SessionUtil vv
        ResponseEntity<?> validation = SessionUtil.validateUser(userRepository, request);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;

        User user = (User) validation.getBody();


        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody PostCreateDTO dto, HttpServletRequest request) {
        ResponseEntity<?> validation = validateUser(request);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;
        User user = (User) validation.getBody();

        Post post = new Post();
        post.setAuthor(user);
        post.setCaption(dto.caption);
        post.setImageUrl(dto.imageUrl);
        post.setCreatedAt(LocalDateTime.now());

        postRepository.save(post);

        return ResponseEntity.ok(Map.of("message", "Post created successfully"));
    }

    @GetMapping
    public ResponseEntity<?> getAllPosts() {
        List<Post> posts = postRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        List<PostResponseDTO> response = posts.stream().map(PostResponseDTO::new).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(@PathVariable Integer id) {
        Optional<Post> postOpt = postRepository.findById(id);
        if (postOpt.isEmpty()) return ResponseEntity.status(404).body(Map.of("error", "Post not found"));

        return ResponseEntity.ok(new PostResponseDTO(postOpt.get()));
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<?> likePost(@PathVariable Integer id, HttpServletRequest request) {
        //✅ SessionUtil vv
        ResponseEntity<?> validation = SessionUtil.validateUser(userRepository, request);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;
        User user = (User) validation.getBody();

        Optional<Post> postOpt = postRepository.findById(id);
        if (postOpt.isEmpty()) return ResponseEntity.status(404).body(Map.of("error", "Post not found"));

        Like like = new Like();
        like.setUser(user);
        like.setPost(postOpt.get());
        like.setLikedAt(LocalDateTime.now());
        postOpt.get().getLikes().add(like);

        postRepository.save(postOpt.get());
        return ResponseEntity.ok(Map.of("message", "Post liked"));
    }

    @DeleteMapping("/{id}/like")
    public ResponseEntity<?> unlikePost(@PathVariable Integer id, HttpServletRequest request) {
        ResponseEntity<?> validation = SessionUtil.validateUser(userRepository, request);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;
        User user = (User) validation.getBody();

        Optional<Post> postOpt = postRepository.findById(id);
        if (postOpt.isEmpty()) return ResponseEntity.status(404).body(Map.of("error", "Post not found"));

        Post post = postOpt.get();
        Optional<Like> likeOpt = post.getLikes().stream()
                .filter(like -> {
                    assert user != null;
                    return like.getUser().getUserId().equals(user.getUserId());
                })
                .findFirst();

        if (likeOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "You haven't liked this post"));
        }

        post.getLikes().remove(likeOpt.get());
        postRepository.save(post);
        return ResponseEntity.ok(Map.of("message", "Post unliked"));
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<?> commentPost(@PathVariable Integer id, @RequestBody Map<String, String> body, HttpServletRequest request) {
        ResponseEntity<?> validation = SessionUtil.validateUser(userRepository, request);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;
        User user = (User) validation.getBody();

        Optional<Post> postOpt = postRepository.findById(id);
        if (postOpt.isEmpty()) return ResponseEntity.status(404).body(Map.of("error", "Post not found"));

        String content = body.get("content");
        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Comment content required"));
        }

        Comment comment = new Comment();
        comment.setAuthor(user);
        comment.setPost(postOpt.get());
        comment.setContent(content);
        comment.setCreatedAt(LocalDateTime.now());
        postOpt.get().getComments().add(comment);

        postRepository.save(postOpt.get());
        return ResponseEntity.ok(Map.of("message", "Comment added"));
    }

    @PostMapping("/{id}/share")
    public ResponseEntity<?> sharePost(@PathVariable Integer id, HttpServletRequest request) {
        ResponseEntity<?> validation = SessionUtil.validateUser(userRepository, request);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;
        User user = (User) validation.getBody();

        Optional<Post> postOpt = postRepository.findById(id);
        if (postOpt.isEmpty()) return ResponseEntity.status(404).body(Map.of("error", "Post not found"));

        Share share = new Share();
        share.setUser(user);
        share.setPost(postOpt.get());
        share.setSharedAt(LocalDateTime.now());
        postOpt.get().getShares().add(share);

        postRepository.save(postOpt.get());
        return ResponseEntity.ok(Map.of("message", "Post shared"));
    }



}
