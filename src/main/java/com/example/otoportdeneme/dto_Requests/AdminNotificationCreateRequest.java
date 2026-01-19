package com.example.otoportdeneme.dto_Requests;

import com.example.otoportdeneme.Enums.NotificationType;

public class AdminNotificationCreateRequest {

    private Long storeId;
    private NotificationType type;
    private String title;
    private String message;
    private String payloadJson;

    public AdminNotificationCreateRequest() {}

    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getPayloadJson() { return payloadJson; }
    public void setPayloadJson(String payloadJson) { this.payloadJson = payloadJson; }
}
