package com.example.springservice;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
        User user = authService.register(name, email, password);
        SessionUtil.storeUserSession(httpRequest, user);

        return ResponseEntity.ok(Map.of("message", "User registered", "userId", user.getId()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request, HttpServletRequest httpRequest) {
        System.out.println("----> 🟢 POST /auth/login called");

        return authService.authenticate(request.get("email"), request.get("password"))
                .map(user -> {
                    SessionUtil.storeUserSession(httpRequest, user);
                    return ResponseEntity.ok(Map.of(
                            "message", "Login successful",
                            "userId", user.getId()
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
                    "id", user.getId(),
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

        Optional<User> userOptional = authService.getUserById(id);
        return userOptional.map(user -> ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "role", user.getRole()
        ))).orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "User not found")));
    }

    // ✅ API Email
    @GetMapping("/user/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        System.out.println("----> 🟢 GET /auth/user/email/" + email + " called");

        Optional<User> userOptional = authService.getUserByEmail(email);
        return userOptional.map(user -> ResponseEntity.ok(Map.of(
                "id", user.getId(),
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
