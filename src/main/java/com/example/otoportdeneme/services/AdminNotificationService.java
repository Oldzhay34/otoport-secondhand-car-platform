package com.example.otoportdeneme.services;

import com.example.otoportdeneme.Enums.NotificationType;

public interface AdminNotificationService {
    void sendToStore(Long storeId, NotificationType type, String title, String message, String payloadJson);
    void broadcastToAllStores(NotificationType type, String title, String message, String payloadJson);
}
