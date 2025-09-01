package com.example.springservice.controller;

import com.example.springservice.SessionUtil;
import com.example.springservice.dto.PostCreateDTO;
import com.example.springservice.dto.PostResponseDTO;
import com.example.springservice.entites.*;
import com.example.springservice.repo.*;
import com.example.springservice.repo.UserFollowsRepository;
import com.example.springservice.repo.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    @Autowired
    private UserFollowsRepository  userFollowsRepository;
//======================================= 🛠️ C 🛠️ ======================================================//
    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody PostCreateDTO dto, HttpServletRequest request) {
        User user = SessionUtil.requireSessionUser(userRepository, request);
        System.out.println("--->  \uD83D\uDE80 ' "+user.getName()+" ' Creating post....");
        Post post = new Post();
        post.setAuthor(user);
        post.setCaption(dto.caption);
        post.setImageUrl(dto.imageUrl);
        post.setCreatedAt(LocalDateTime.now());

        postRepository.save(post);

        return ResponseEntity.ok(Map.of("message", "Post created successfully"));
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<?> likePost(@PathVariable Integer id, HttpServletRequest request) {
        User user = SessionUtil.requireSessionUser(userRepository, request);

        Optional<Post> postOpt = postRepository.findById(id);
        if (postOpt.isEmpty()) return ResponseEntity.status(404).body(Map.of("error", "Post not found"));

        try {
            Like like = new Like();
            like.setUser(user);
            like.setPost(postOpt.get());
            like.setLikedAt(LocalDateTime.now());
            postOpt.get().getLikes().add(like);

            postRepository.save(postOpt.get());
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("error", "u already like d post"));
        }
        return ResponseEntity.ok(Map.of("message", "Post liked"));
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<?> commentPost(@PathVariable Integer id, @RequestBody Map<String, String> body, HttpServletRequest request) {
        User user = SessionUtil.requireSessionUser(userRepository, request);

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

//======================================= 🛠️ R 🛠️ ======================================================//

    @GetMapping
    public ResponseEntity<?> getAllPosts(HttpServletRequest request) {
        User user = SessionUtil.requireSessionUser(userRepository, request);

        List<Post> posts = postRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        List<PostResponseDTO> response = posts.stream()
                .map(post -> new PostResponseDTO(post, user.getUserId()))
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(@PathVariable Integer id, HttpServletRequest request) {
        User user = SessionUtil.requireSessionUser(userRepository, request);

        Optional<Post> postOpt = postRepository.findById(id);
        if (postOpt.isEmpty()) return ResponseEntity.status(404).body(Map.of("error", "Post not found"));

        return ResponseEntity.ok(new PostResponseDTO(postOpt.get(), user.getUserId()));
    }

    @GetMapping("/feed")
    public ResponseEntity<?> getFeed(HttpServletRequest request) {
        User user = SessionUtil.requireSessionUser(userRepository, request);

        List<UserFollows> following = userFollowsRepository.findAllByFollower_UserId(user.getUserId());
        List<Integer> followingIds = following.stream()
                .map(f -> f.getFollowing().getUserId())
                .toList();

        if (followingIds.isEmpty()) return ResponseEntity.ok(List.of());

        List<Post> posts = postRepository.findAllByAuthor_UserIdInOrderByCreatedAtDesc(followingIds);
        List<PostResponseDTO> response = posts.stream()
                .map(post -> new PostResponseDTO(post, user.getUserId()))
                .toList();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/share")
    public ResponseEntity<?> sharePost(@PathVariable Integer id, HttpServletRequest request) {
        User user = SessionUtil.requireSessionUser(userRepository, request);

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

//======================================= 🛠️ U 🛠️ ======================================================//

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(@PathVariable Integer id, @RequestBody PostCreateDTO dto, HttpServletRequest request) {
        User user = SessionUtil.requireSessionUser(userRepository, request);
        Post post = getVerifiedPost(id, user);

        post.setCaption(dto.caption);
        post.setImageUrl(dto.imageUrl);
        postRepository.save(post);

        return ResponseEntity.ok(Map.of("message", "Post updated"));
    }

//======================================= 🛠️ D 🛠️ ======================================================//

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Integer id, HttpServletRequest request) {
        User user = SessionUtil.requireSessionUser(userRepository, request);
        Post post = getVerifiedPost(id, user);

        postRepository.delete(post);
        return ResponseEntity.ok(Map.of("message", "Post deleted"));
    }

    @DeleteMapping("/{id}/like")
    public ResponseEntity<?> unlikePost(@PathVariable Integer id, HttpServletRequest request) {
        User user = SessionUtil.requireSessionUser(userRepository, request);

        Optional<Post> postOpt = postRepository.findById(id);
        if (postOpt.isEmpty()) return ResponseEntity.status(404).body(Map.of("error", "Post not found"));

        Post post = postOpt.get();
        Optional<Like> likeOpt = post.getLikes().stream()
                .filter(like -> like.getUser().getUserId().equals(user.getUserId()))
                .findFirst();

        if (likeOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "You haven't liked this post"));
        }

        post.getLikes().remove(likeOpt.get());
        postRepository.save(post);
        return ResponseEntity.ok(Map.of("message", "Post unliked"));
    }

//======================================= ♻️ Reuse ♻️ ======================================================//
    private Post getVerifiedPost (Integer id, User user) {
        Optional<Post> postOpt = postRepository.findById(id);
        if (postOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
        }
        Post post = postOpt.get();
        if (!post.getAuthor().getUserId().equals(user.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your post");
        }
        return post;
    }
}
