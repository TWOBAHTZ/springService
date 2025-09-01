package com.example.springservice.repo;

import com.example.springservice.entites.Notification;
import com.example.springservice.entites.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findAllByUserOrderByCreatedAtDesc(User user);
    List<Notification> findAllByUserAndIsReadFalse(User user);
//    List<Notification> findAllByUser_UserIdOrderByCreatedAtDesc(Integer userId);
//    List<Notification> findUnreadByUser(User user);

    long countByUserAndIsReadFalse(User user);
}
