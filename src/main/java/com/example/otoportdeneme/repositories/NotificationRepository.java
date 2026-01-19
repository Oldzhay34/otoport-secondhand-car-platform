package com.example.otoportdeneme.repositories;


import com.example.otoportdeneme.Enums.RecipientType;
import com.example.otoportdeneme.models.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientTypeAndRecipientIdOrderByCreatedAtDesc(RecipientType type, Long recipientId);
    List<Notification> findByRecipientTypeAndRecipientIdAndIsReadOrderByCreatedAtDesc(RecipientType type, Long recipientId, Boolean isRead);

    long countByRecipientTypeAndRecipientIdAndIsRead(RecipientType type, Long recipientId, Boolean isRead);
}

