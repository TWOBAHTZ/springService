package com.example.springservice;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

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

        return ResponseEntity.ok(Map.of("message", "User registered", "userId", user.getUser_id()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request, HttpServletRequest httpRequest) {
        System.out.println("----> 🟢 POST /auth/login called");

        return authService.authenticate(request.get("email"), request.get("password"))
                .map(user -> {
                    SessionUtil.storeUserSession(httpRequest, user);
                    return ResponseEntity.ok(Map.of(
                            "message", "Login successful",
                            "userId", user.getUser_id()
                    ));
                })
                .orElse(ResponseEntity.status(401).body(Map.of("message", "Invalid credentials")));
    }


    @GetMapping("/session")
    public ResponseEntity<?> session(HttpServletRequest request) {
        System.out.println("----> 🟢 GET /auth/session called");

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");
            return ResponseEntity.ok(Map.of("user", Map.of(
                    "id", user.getUser_id(),
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

    // ✅ API  ID
    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUserById(@PathVariable String id) {
        System.out.println("----> 🟢 GET /auth/user/" + id + " called");

        try {
            Optional<User> userOptional = authService.getUserById(Integer.valueOf(id));

            if (userOptional.isPresent()) {
                User user = userOptional.get();

                // Logging เพื่อตรวจสอบค่า
                System.out.println("Fetched user: " + user);
                System.out.println("Email: " + user.getEmail());

                // ใช้ HashMap แทน Map.of เพื่อกัน null
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("id", user.getUser_id());
                userMap.put("name", user.getName());
                userMap.put("email", user.getEmail());
                userMap.put("role", user.getRole());

                return ResponseEntity.ok(userMap);
            } else {
                return ResponseEntity.status(404).body(Map.of("message", "User not found"));
            }

        } catch (Exception e) {
            e.printStackTrace(); // log error
            return ResponseEntity.status(500).body(Map.of(
                    "message", "Internal Server Error",
                    "error", e.getMessage()
            ));
        }
    }


    // ✅ API Email
    @GetMapping("/user/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        System.out.println("----> 🟢 GET /auth/user/email/" + email + " called");

        Optional<User> userOptional = authService.getUserByEmail(email);
        return userOptional.map(user -> ResponseEntity.ok(Map.of(
                "id", user.getUser_id(),
                "name", user.getName(),
                "email", user.getEmail(),
                "role", user.getRole()
        ))).orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "User not found")));
    }
}


class SessionUtil {
    public static void storeUserSession(HttpServletRequest request, User user) {
        if (user == null) return; // ✅  user shout not null

        HttpSession session = request.getSession(true);
        session.setAttribute("user", user);
        //session.setMaxInactiveInterval(30 * 60);  ✅  session timeout  30 min
    }
    public static void clearUserSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}
