package com.example.otoportdeneme.services;

import com.example.otoportdeneme.Enums.NotificationType;
import com.example.otoportdeneme.Interfaces.NotifiableRecipient;

public interface NotificationService {
    void notify(NotifiableRecipient recipient, NotificationType type, String title, String message, String payloadJson);
}
