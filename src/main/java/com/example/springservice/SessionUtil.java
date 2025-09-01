package com.example.springservice;

import com.example.springservice.entites.User;
import com.example.springservice.repo.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Optional;

public class SessionUtil {
    public static void storeUserSession(HttpServletRequest request, User user) { // ✅  user shout not null
        if (user == null) return;
        HttpSession session = request.getSession(true);
        session.setAttribute("user", user);
        session.setMaxInactiveInterval(30 * 60);  //✅  session timeout  30 min
    }
    public static void clearUserSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
    public static Integer getUserIdFromSession( HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;

        User user = (User) session.getAttribute("user");
        return user != null ? user.getUserId() : null;
    }
//    public static ResponseEntity<?> validateUser(UserRepository userRepository, HttpServletRequest request) { //✅ Ready to set role / status / ban by sessionID
//        Integer userId = getUserIdFromSession(request);
//        if (userId == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
//
//        Optional<User> userOpt = userRepository.findById(userId);
//        if (userOpt.isEmpty()) return ResponseEntity.status(404).body(Map.of("error", "User not found"));
//
//        return ResponseEntity.ok(userOpt.get());
//    }

    public static User requireSessionUser(UserRepository userRepository, HttpServletRequest request) {
        Integer userId = getUserIdFromSession(request);
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        return userRepository.findById(userId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        );
    }

}