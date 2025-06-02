package com.example.springservice.controller;

import com.example.springservice.SessionUtil;
import com.example.springservice.entites.*;
import com.example.springservice.repo.*;
import com.example.springservice.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    public AuthController(AuthService authService , UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    //======================================= 🛠️ C 🛠️ ======================================================//
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request, HttpServletRequest httpRequest) {
        System.out.println("----> 🟢 POST /auth/register called");
        String name = request.get("name");
        String email = request.get("email");
        String password = request.get("password");
        String role = request.get("role");

        if (name == null || email == null || password == null || role == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Missing required fields"));
        }
        if (authService.userExists(email)) {
            return ResponseEntity.status(409).body(Map.of("message", "Email already registered"));
        }
        User user = authService.register(name, email, password, role);
        SessionUtil.storeUserSession(httpRequest, user);

        return ResponseEntity.ok(Map.of("message", "User registered", "userId", user.getUserId()));
    }

    //======================================= 🛠️ R 🛠️ ======================================================//
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request, HttpServletRequest httpRequest) {
        System.out.println("----> 🟢 POST /auth/login called");

        return authService.authenticate(request.get("email"), request.get("password"))
                .map(user -> {
                    SessionUtil.storeUserSession(httpRequest, user);
                    return ResponseEntity.ok(Map.of(
                            "message", "Login successful",
                            "userId", user.getUserId()
                    ));
                })
                .orElse(ResponseEntity.status(401).body(Map.of("message", "Invalid credentials")));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUserById(@PathVariable String id) {
        System.out.println("----> 🟢 GET /auth/user/" + id + " called");

        Optional<User> userOptional = authService.getUserById(Integer.valueOf(id));
        return userOptional.map(user -> ResponseEntity.ok(Map.of(
                "id", user.getUserId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "role", user.getRole()
        ))).orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "User not found")));
    }

    @GetMapping("/user/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        System.out.println("----> 🟢 GET /auth/user/email/" + email + " called");

        Optional<User> userOptional = authService.getUserByEmail(email);
        return userOptional.map(user -> ResponseEntity.ok(Map.of(
                "id", user.getUserId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "role", user.getRole()
        ))).orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "User not found")));
    }

    //======================================= 🛠️ U 🛠️ ======================================================//
    @GetMapping("/session")
    public ResponseEntity<?> session(HttpServletRequest request) {
        System.out.println("----> 🟢 GET /auth/session called");

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");
            return ResponseEntity.ok(Map.of("user", Map.of(
                    "id", user.getUserId(),
                    "email", user.getEmail()
            )));
        }
        return ResponseEntity.status(401).body(Map.of("message", "Not logged in"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        System.out.println("----> 🟢 POST /auth/logout called");
        SessionUtil.clearUserSession(request);
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

//======================================= 🛠️ D 🛠️ ======================================================//
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteAccount(HttpServletRequest request) {
        //✅ SessionUtil vv
        Integer userId = SessionUtil.getUserIdFromSession(request);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
        }

        userRepository.deleteById(userId);
        SessionUtil.clearUserSession(request);

        return ResponseEntity.ok(Map.of("message", "Account deleted successfully"));
    }



    }



