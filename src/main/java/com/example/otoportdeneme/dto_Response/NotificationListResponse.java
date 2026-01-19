package com.example.otoportdeneme.dto_Response;

import com.example.otoportdeneme.dto_Objects.NotificationDto;
import java.util.List;

public class NotificationListResponse {
    private List<NotificationDto> items;

    public NotificationListResponse() {}
    public NotificationListResponse(List<NotificationDto> items) { this.items = items; }

    public List<NotificationDto> getItems() { return items; }
    public void setItems(List<NotificationDto> items) { this.items = items; }
}
