package com.example.otoportdeneme.controllers;

import com.example.otoportdeneme.Enums.RecipientType;
import com.example.otoportdeneme.dto_Objects.NotificationDto;
import com.example.otoportdeneme.dto_Objects.NotificationMapper;
import com.example.otoportdeneme.dto_Response.NotificationListResponse;
import com.example.otoportdeneme.dto_Response.UnreadCountResponse;
import com.example.otoportdeneme.models.Notification;
import com.example.otoportdeneme.repositories.NotificationRepository;
import com.example.otoportdeneme.repositories.StoreRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/store/notifications")
public class StoreNotificationController {

    private final NotificationRepository notificationRepository;
    private final StoreRepository storeRepository;

    public StoreNotificationController(NotificationRepository notificationRepository,
                                       StoreRepository storeRepository) {
        this.notificationRepository = notificationRepository;
        this.storeRepository = storeRepository;
    }

    @GetMapping
    public NotificationListResponse list(Authentication auth,
                                         @RequestParam(value = "unreadOnly", required = false) Boolean unreadOnly) {

        Long storeId = resolveStoreId(auth);

        List<Notification> items;
        if (Boolean.TRUE.equals(unreadOnly)) {
            items = notificationRepository.findByRecipientTypeAndRecipientIdAndIsReadOrderByCreatedAtDesc(
                    RecipientType.STORE, storeId, false
            );
        } else {
            items = notificationRepository.findByRecipientTypeAndRecipientIdOrderByCreatedAtDesc(
                    RecipientType.STORE, storeId
            );
        }

        List<NotificationDto> dtos = items.stream()
                .map(NotificationMapper::toDto)
                .toList();

        return new NotificationListResponse(dtos);
    }

    @GetMapping("/unread-count")
    public UnreadCountResponse unreadCount(Authentication auth) {
        Long storeId = resolveStoreId(auth);

        long count = notificationRepository.countByRecipientTypeAndRecipientIdAndIsRead(
                RecipientType.STORE, storeId, false
        );

        return new UnreadCountResponse(count);
    }

    @PatchMapping("/{id}/read")
    @Transactional
    public void markRead(Authentication auth, @PathVariable Long id) {
        Long storeId = resolveStoreId(auth);

        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));

        // güvenlik: sadece kendi notifini işaretleyebilsin
        if (n.getRecipientType() != RecipientType.STORE || !n.getRecipientId().equals(storeId)) {
            throw new IllegalArgumentException("Forbidden");
        }

        if (Boolean.FALSE.equals(n.getIsRead())) {
            n.setIsRead(true);
            n.setReadAt(Instant.now());
            notificationRepository.save(n);
        }
    }

    @PatchMapping("/read-all")
    @Transactional
    public void markAllRead(Authentication auth) {
        Long storeId = resolveStoreId(auth);

        List<Notification> unread = notificationRepository
                .findByRecipientTypeAndRecipientIdAndIsReadOrderByCreatedAtDesc(
                        RecipientType.STORE, storeId, false
                );

        Instant now = Instant.now();
        for (Notification n : unread) {
            n.setIsRead(true);
            n.setReadAt(now);
        }
        notificationRepository.saveAll(unread);
    }

    private Long resolveStoreId(Authentication auth) {
        if (auth == null) throw new IllegalArgumentException("Unauthorized");
        String email = auth.getName();
        return storeRepository.findIdByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Store not found"));
    }
}
