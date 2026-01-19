package com.example.otoportdeneme.dto_Response;

public class StoreActivityDto {
    public Long storeId;
    public long listingsCreated;
    public long listingsDeleted;
    public long inquiriesCreated;
    public long messagesSent;
    public long unreadMessages;

    public StoreActivityDto(Long storeId, long created, long deleted, long inquiries, long messages, long unread) {
        this.storeId = storeId;
        this.listingsCreated = created;
        this.listingsDeleted = deleted;
        this.inquiriesCreated = inquiries;
        this.messagesSent = messages;
        this.unreadMessages = unread;
    }
}
