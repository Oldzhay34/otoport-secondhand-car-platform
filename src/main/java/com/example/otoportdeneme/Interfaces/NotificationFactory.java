package com.example.otoportdeneme.Interfaces;

import com.example.otoportdeneme.Enums.NotificationType;
import com.example.otoportdeneme.models.Notification;

public final class NotificationFactory {

    private NotificationFactory() {}

    public static Notification create(
            NotifiableRecipient recipient,
            NotificationType type,
            String title,
            String message,
            String payloadJson
    ) {
        Notification n = new Notification();
        n.setRecipientType(recipient.getRecipientType());
        n.setRecipientId(recipient.getId());
        n.setType(type);
        n.setTitle(title);
        n.setMessage(message);
        n.setPayloadJson(payloadJson);
        n.setIsRead(false);
        return n;
    }
}
