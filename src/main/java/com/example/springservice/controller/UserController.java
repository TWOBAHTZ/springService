package com.example.springservice.controller;

import com.example.springservice.*;
import com.example.springservice.dto.*;
import com.example.springservice.entites.*;
import com.example.springservice.entites.enmap.FollowId;
import com.example.springservice.repo.*;
import com.example.springservice.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthService authService;
    @Autowired
    private UserFollowsRepository userFollowsRepository;
    @Autowired
    private CommissionCardRepository cardRepo;
    @Autowired
    CommissionService commissionService;
    @Autowired
    NotificationService notificationService;
//======================================= 🛠️ C 🛠️ ======================================================//

//---

//======================================= 🛠️ R 🛠️ ======================================================//

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(HttpServletRequest request) {
        System.out.println("----> 🟢 GET User profile called");
        User sessionUser = (User) request.getSession().getAttribute("user");
        if (sessionUser == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
        return ResponseEntity.ok(Map.of("user", new UserProfileDTO(sessionUser)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Integer id) {
        System.out.println("----> 🟢 GET /users/" + id + " called");

        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }
        return ResponseEntity.ok(Map.of("user", new UserProfileDTO(user)));
    }

    @GetMapping("/posts/{userId}") // for profile
    public ResponseEntity<?> getPostsByUserId(@PathVariable Integer userId) {
        System.out.println("----> 🟢 GET User get Post by Id //called//");
        List<Post> posts = postRepository.findAllByAuthor_UserIdOrderByCreatedAtDesc(userId);
        List<PostResponseDTO> response = posts.stream()
                .map(post -> new PostResponseDTO(post, userId))
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchUsersByName(@RequestParam String name, HttpServletRequest request) {
        User currentUser = SessionUtil.requireSessionUser(userRepository, request);
        System.out.println("----> searchUsersByName called");
        List <User> users = userRepository.findByNameContainingIgnoreCase(name);
        List<UserProfileDTO> response = users.stream()
                .map(user -> new UserProfileDTO(user, currentUser.getUserId(), userFollowsRepository))
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/following")
    public ResponseEntity<?> getFollowing(HttpServletRequest request) {
        User user = SessionUtil.requireSessionUser(userRepository, request);

        List<UserFollows> following = userFollowsRepository.findAllByFollower_UserId(user.getUserId());
        List<String> usernames = following.stream().map(f -> f.getFollowing().getName()).toList();

        return ResponseEntity.ok(Map.of("following", usernames));
    }

    @GetMapping("/followers")
    public ResponseEntity<?> getFollowers(HttpServletRequest request) {
        User user = SessionUtil.requireSessionUser(userRepository, request);

        List<UserFollows> followers = userFollowsRepository.findAllByFollowing_UserId(user.getUserId());
        List<String> usernames = followers.stream().map(f -> f.getFollower().getName()).toList();

        return ResponseEntity.ok(Map.of("followers", usernames));
    }

    @GetMapping("/{id}/followers")
    public ResponseEntity<?> getFollowersOfUser(@PathVariable Integer id, HttpServletRequest request) {
        User currentUser = SessionUtil.requireSessionUser(userRepository, request);
        if (!currentUser.getRole().equals("admin") && !currentUser.getUserId().equals(id)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }

        List<UserFollows> followers = userFollowsRepository.findAllByFollowing_UserId(id);
        List<UserProfileDTO> response = followers.stream()
                .map(f -> new UserProfileDTO(f.getFollower(), currentUser.getUserId(), userFollowsRepository))
                .toList();


        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/following")
    public ResponseEntity<?> getFollowingOfUser(@PathVariable Integer id, HttpServletRequest request) {
        User currentUser = SessionUtil.requireSessionUser(userRepository, request);
        if (!currentUser.getRole().equals("admin") && !currentUser.getUserId().equals(id)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }

        List<UserFollows> following = userFollowsRepository.findAllByFollower_UserId(id);
        List<UserProfileDTO> response = following.stream()
                .map(f -> new UserProfileDTO(f.getFollowing(), currentUser.getUserId(), userFollowsRepository))
                .toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/commissions/request")
    public ResponseEntity<?> requestCommission(
            @RequestBody CommissionBriefRequestDTO dto,
            HttpServletRequest request
    ) {
        User customer = SessionUtil.requireSessionUser(userRepository, request);
        CommissionBriefDTO brief = commissionService.createCommissionRequest(dto, customer);

        // 🛎️ ส่ง noti ให้ศิลปินหลังจากได้ brief กลับมา
        cardRepo.findById(dto.cardId).ifPresent(card -> notificationService.sendNotiTo(
                card.getArtist(),
                "NEW_COMMISSION",
                "📥 คำขอใหม่จาก " + customer.getName() + " เรื่อง: " + card.getTitle()
        ));

        return ResponseEntity.ok(Map.of(
                "message", "Commission request submitted",
                "brief", brief
        ));
    }
    @GetMapping("/commissions/{id}")
    public ResponseEntity<?> getMyCommissionDetail(@PathVariable Integer id, HttpServletRequest request) {
        User user = SessionUtil.requireSessionUser(userRepository, request);
        return ResponseEntity.ok(commissionService.getDetail(id, user));
    }

//======================================= 🛠️ U 🛠️ ======================================================//

    @PatchMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> body, HttpServletRequest request) {
        String oldPass = body.get("oldPassword");
        String newPass = body.get("newPassword");

        Integer userId = SessionUtil.getUserIdFromSession(request);
        if (userId == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) return ResponseEntity.status(404).body(Map.of("error", "User not found"));

        User user = userOpt.get();
        if (!authService.matchesPassword(oldPass, user.getPassword())) return ResponseEntity.badRequest().body(Map.of("error", "Incorrect old password"));

        user.setPassword(authService.encodePassword(newPass));
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }

    @PatchMapping("/change-email")
    public ResponseEntity<?> changeEmail(@RequestBody Map<String, String> body, HttpServletRequest request) {
        String newEmail = body.get("email");

        Integer userId = SessionUtil.getUserIdFromSession(request);
        if (userId == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

        if (userRepository.findByEmail(newEmail).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email already in use"));
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return ResponseEntity.status(404).body(Map.of("error", "User not found"));

        user.setEmail(newEmail);
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Email updated successfully"));
    }

    @PostMapping("/refresh-session")
    public ResponseEntity<?> refreshSession(HttpServletRequest request) {
        User user = SessionUtil.requireSessionUser(userRepository, request);
        SessionUtil.storeUserSession(request, user);
        return ResponseEntity.ok(Map.of("message", "Session refreshed", "user", new UserProfileDTO(user)));
    }

    @PostMapping("/{id}/follow")
    public ResponseEntity<?> followUser(@PathVariable Integer id, HttpServletRequest request) {
        User follower = SessionUtil.requireSessionUser(userRepository, request);

        if (follower.getUserId().equals(id)) {
            return ResponseEntity.badRequest().body(Map.of("error", "You cannot follow yourself"));
        }

        Optional<User> followingOpt = userRepository.findById(id);
        if (followingOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }

        User following = followingOpt.get();
        FollowId followId = new FollowId();
        followId.setFollowerId(follower.getUserId());
        followId.setFollowingId(following.getUserId());

        if (userFollowsRepository.existsById(followId)) {
            return ResponseEntity.ok(Map.of("message", "Already following"));
        }

        UserFollows follow = new UserFollows();
        follow.setFollower(follower);
        follow.setFollowing(following);
        follow.setFollowDate(LocalDateTime.now());
        userFollowsRepository.save(follow);

        notificationService.send(
                following, // --- target
                follower,  // --- actor
                Notification.NotificationType.FOLLOW,
                follower.getName() + " followed you"
        );

        return ResponseEntity.ok(Map.of("message", "Followed successfully"));
    }

    @DeleteMapping("/{id}/unfollow")
    public ResponseEntity<?> unfollowUser(@PathVariable Integer id, HttpServletRequest request) {
        User follower = SessionUtil.requireSessionUser(userRepository, request);

        FollowId followId = new FollowId();
        followId.setFollowerId(follower.getUserId());
        followId.setFollowingId(id);

        if (!userFollowsRepository.existsById(followId)) {
            return ResponseEntity.badRequest().body(Map.of("error", "You are not following this user"));
        }

        userFollowsRepository.deleteById(followId);
        return ResponseEntity.ok(Map.of("message", "Unfollowed successfully"));
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody User updatedUser, HttpServletRequest request) {
        System.out.println("----> 🟡 PUT /user/profile called");

        Integer userId = SessionUtil.getUserIdFromSession(request);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
        }

        return userRepository.findById(userId)
                .map(existingUser -> {
                    applyUserUpdates(existingUser, updatedUser);
                    userRepository.save(existingUser);
                    return ResponseEntity.ok(Map.of(
                            "message", "Profile updated successfully",
                            "user", new UserProfileDTO(existingUser)
                    ));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found")));
    }

//======================================= 🛠️ D 🛠️ ======================================================//

//---



//======================================= ♻️ Reuse ♻️ ======================================================//

    private void applyUserUpdates(User existing, User update) {
        if (isValid(update.getName())) {
            existing.setName(update.getName().trim());
        }

        if (isValid(update.getProfile_picture())) {
            existing.setProfile_picture(update.getProfile_picture().trim());
        }

        if (isValid(update.getDescription())) {
            existing.setDescription(update.getDescription().trim());
        }

        if (update.getCommission_status() != null) {
            existing.setCommission_status(update.getCommission_status());
        }
    }

    private boolean isValid(String value) {
        return value != null && !value.trim().isEmpty();
    }

}

