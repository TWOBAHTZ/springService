package com.example.springservice.dto;

import com.example.springservice.entites.Notification;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter

public class NotificationDTO {
    public Integer notificationId;
    public String message;
    public String type;
    public Boolean isRead;
    public String createdAt;

    public NotificationDTO(Notification n) {
        this.notificationId = n.getNotificationId();
        this.message = n.getMessage();
        this.type = n.getType().name();
        this.isRead = n.getIsRead();
        this.createdAt = n.getCreatedAt().toString();
    }
}



