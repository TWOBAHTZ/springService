package com.example.springservice.controller;

import com.example.springservice.SessionUtil;
import com.example.springservice.dto.NotificationDTO;
import com.example.springservice.entites.Notification;
import com.example.springservice.entites.User;
import com.example.springservice.repo.NotificationRepository;
import com.example.springservice.repo.UserRepository;
import com.example.springservice.service.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private NotificationRepository notificationRepository;

    //User session
    private User getSessionUser(HttpServletRequest request) {
        return SessionUtil.requireSessionUser(userRepository, request);
    }

    @GetMapping
    public ResponseEntity<?> getNotifications(HttpServletRequest request) {
        User user = getSessionUser(request);

        List<NotificationDTO> notifications = notificationService.getUserNotifications(user);
        long unreadCount = notificationService.countUnreadNotifications(user);

        return ResponseEntity.ok(Map.of(
                "notifications", notifications,
                "unreadCount", unreadCount
        ));
    }

    @PostMapping("/read")
    public ResponseEntity<?> markNotificationsAsRead(HttpServletRequest request) {
        User user = getSessionUser(request);

        notificationService.markAllAsRead(user);
        return ResponseEntity.ok(Map.of("message", "All notifications marked as read"));
    }

    @PutMapping("/{id}/read")
    @Transactional
    public ResponseEntity<?> markOneAsRead(@PathVariable Integer id, HttpServletRequest request) {
        User user = getSessionUser(request);

        Notification noti = notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found"));

        if (!noti.getUser().getUserId().equals(user.getUserId())) {
            throw new AccessDeniedException("Access denied");
        }

        if (!noti.getIsRead()) {
            noti.setIsRead(true);
            notificationRepository.save(noti);
        }

        return ResponseEntity.ok(Map.of("message", "Notification marked as read"));
    }
}

