package com.example.otoportdeneme.services;

import com.example.otoportdeneme.Enums.NotificationType;
import com.example.otoportdeneme.Interfaces.NotificationFactory;
import com.example.otoportdeneme.Interfaces.NotifiableRecipient;
import com.example.otoportdeneme.repositories.NotificationRepository;
import jakarta.transaction.Transactional;
import com.example.otoportdeneme.models.Notification;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    @Transactional
    public void notify(NotifiableRecipient recipient, NotificationType type, String title, String message, String payloadJson) {
        Notification n = NotificationFactory.create(recipient, type, title, message, payloadJson);
        notificationRepository.save(n);
    }
}
