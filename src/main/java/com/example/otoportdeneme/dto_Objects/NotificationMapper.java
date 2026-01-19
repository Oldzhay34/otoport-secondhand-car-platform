package com.example.otoportdeneme.dto_Objects;

import com.example.otoportdeneme.models.Notification;

public class NotificationMapper {
    private NotificationMapper(){}

    public static NotificationDto toDto(Notification n){
        NotificationDto d = new NotificationDto();
        d.setId(n.getId());
        d.setType(n.getType() != null ? n.getType().name() : null);
        d.setTitle(n.getTitle());
        d.setMessage(n.getMessage());
        d.setPayloadJson(n.getPayloadJson());
        d.setIsRead(n.getIsRead());
        d.setReadAt(n.getReadAt());
        d.setCreatedAt(n.getCreatedAt());
        return d;
    }
}
