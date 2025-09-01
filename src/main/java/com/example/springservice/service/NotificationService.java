package com.example.springservice.service;

import com.example.springservice.dto.NotificationDTO;
import com.example.springservice.entites.Notification;
import com.example.springservice.entites.User;
import com.example.springservice.entites.UserFollows;
import com.example.springservice.repo.NotificationRepository;
import com.example.springservice.repo.UserFollowsRepository;
import com.example.springservice.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Autowired
    private UserFollowsRepository userFollowsRepository;

    // ========================== Utility Methods ==========================

    private Notification.NotificationType toNotificationType(String type) {
        try {
            return Notification.NotificationType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid notification type: " + type);
        }
    }

    private boolean isInvalidNotification(User receiver, User actor) {
        return receiver == null || actor == null || receiver.getUserId().equals(actor.getUserId());
    }
    private void logWarning() {
        System.err.println("[Warning] " + "Notification target or type is null. Skipping notification.");
    }

    // ========================== Core Send Methods ==========================
                      //✅ none Actor
    private void send(User target, Notification.NotificationType type, String message) {
        send(target, null, type, message);
    }
                      //✅ have Actor
                      public void send(User target, User actor, Notification.NotificationType type, String message) {
        if (target == null || type == null) {
            logWarning();
            return;
        }

        Notification noti = new Notification();
        noti.setUser(target);
        noti.setActor(actor);
        noti.setType(type);
        noti.setMessage(message);
        noti.setIsRead(false);
        noti.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(noti);
    }

    // ========================== Public Endpoints ==========================


    public void sendNotiTo(User recipient, String type, String message) {
        Notification.NotificationType notificationType = toNotificationType(type); // Convert string to ENUM
        send(recipient, notificationType, message);
    }

    public void sendNotiAll(List<User> recipients, String type, String message) {
        Notification.NotificationType notificationType = toNotificationType(type); // Convert string to ENUM
        recipients.parallelStream()
                .forEach(recipient -> send(recipient, notificationType, message));
    }

    public void sendNotiAll(List<User> recipients, User actor, String type,String message) {
        Notification.NotificationType notificationType = toNotificationType(type); // Convert string to ENUM
        recipients.parallelStream()
                .forEach(recipient -> send(recipient,actor, notificationType, message));
    }

    public void sendToAllFollowers(User artist, User actor, String type, String message ) {
        List<User> followers = userFollowsRepository
                .findAllByFollowing_UserId(artist.getUserId())
                .stream()
                .map(UserFollows::getFollower)
                .toList();

        sendNotiAll(followers,actor, type, message);
    }



    public List<NotificationDTO> getUserNotifications(User user) {
        return notificationRepository.findAllByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(NotificationDTO::new)
                .toList();
    }

    public long countUnreadNotifications(User user) {
        return notificationRepository.countByUserAndIsReadFalse(user);
    }

    public void markAllAsRead(User user) {
        List<Notification> unread = notificationRepository.findAllByUserAndIsReadFalse(user);
        unread.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(unread);
    }

    public void notifyFollow(User receiver, User actor) {
        if (isInvalidNotification(receiver, actor)) return; // อ่านง่ายขึ้น
        send(receiver, actor, Notification.NotificationType.FOLLOW,
                actor.getName() + " started following you");
    }

    public void notifyLike(User receiver, User actor, Integer postId) {
        if (isInvalidNotification(receiver, actor)) return;
        send(receiver, actor, Notification.NotificationType.LIKE,
                actor.getName() + " liked your post (ID: " + postId + ")");
    }

    public void notifyComment(User receiver, User actor, Integer postId, String commentContent) {
        if (isInvalidNotification(receiver, actor)) return;
        send(receiver, actor, Notification.NotificationType.COMMENT,
                actor.getName() + " commented on your post (ID: " + postId + "): \"" + commentContent + "\"");
    }
}

