package com.example.otoportdeneme.services;

import com.example.otoportdeneme.Enums.NotificationType;
import com.example.otoportdeneme.Enums.RecipientType;
import com.example.otoportdeneme.repositories.StoreRepository;
import org.springframework.stereotype.Service;

@Service
public class AdminNotificationServiceImpl implements AdminNotificationService {

    private final NotificationService notificationService; // senin NotificationServiceImpl zaten var
    private final StoreRepository storeRepository;

    public AdminNotificationServiceImpl(NotificationService notificationService,
                                        StoreRepository storeRepository) {
        this.notificationService = notificationService;
        this.storeRepository = storeRepository;
    }

    @Override
    public void sendToStore(Long storeId, NotificationType type, String title, String message, String payloadJson) {
        if (storeId == null) throw new IllegalArgumentException("storeId required");
        if (title == null || title.trim().isEmpty()) throw new IllegalArgumentException("title required");

        var recipient = new RecipientRef(storeId, RecipientType.STORE);
        notificationService.notify(recipient, type, title.trim(), message, payloadJson);
    }

    @Override
    public void broadcastToAllStores(NotificationType type, String title, String message, String payloadJson) {
        if (title == null || title.trim().isEmpty()) throw new IllegalArgumentException("title required");

        // StoreRepository JpaRepository ise findAll() vardır
        var stores = storeRepository.findAll();
        for (var s : stores) {
            var recipient = new RecipientRef(s.getId(), RecipientType.STORE);
            notificationService.notify(recipient, type, title.trim(), message, payloadJson);
        }
    }
}
