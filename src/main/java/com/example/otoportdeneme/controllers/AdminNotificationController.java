package com.example.otoportdeneme.controllers;

import com.example.otoportdeneme.Enums.NotificationType;
import com.example.otoportdeneme.Enums.RecipientType;
import com.example.otoportdeneme.dto_Requests.AdminNotificationCreateRequest;
import com.example.otoportdeneme.models.Notification;
import com.example.otoportdeneme.repositories.NotificationRepository;
import jakarta.transaction.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/admin/notifications")
public class AdminNotificationController {

    private final NotificationRepository notificationRepository;

    public AdminNotificationController(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @PostMapping
    @Transactional
    public void create(@RequestBody AdminNotificationCreateRequest req) {
        if (req.getStoreId() == null) throw new IllegalArgumentException("storeId is required");
        if (req.getType() == null) req.setType(NotificationType.SYSTEM);
        if (req.getTitle() == null || req.getTitle().trim().isEmpty()) throw new IllegalArgumentException("title is required");

        Notification n = new Notification();
        n.setRecipientType(RecipientType.STORE);
        n.setRecipientId(req.getStoreId());
        n.setType(req.getType());
        n.setTitle(req.getTitle().trim());
        n.setMessage(req.getMessage());
        n.setPayloadJson(req.getPayloadJson());
        n.setIsRead(false);
        n.setReadAt(null);
        n.setCreatedAt(Instant.now());

        notificationRepository.save(n);
    }
}
