package com.example.otoportdeneme.dto_Objects;

import java.time.Instant;

public class InquiryArchivedMessageDto {
    private Instant sentAt;
    private String sender;     // "CLIENT" | "STORE"
    private Long senderId;     // clientId veya storeId
    private String content;

    public InquiryArchivedMessageDto() {}

    public InquiryArchivedMessageDto(Instant sentAt, String sender, Long senderId, String content) {
        this.sentAt = sentAt;
        this.sender = sender;
        this.senderId = senderId;
        this.content = content;
    }

    public Instant getSentAt() { return sentAt; }
    public void setSentAt(Instant sentAt) { this.sentAt = sentAt; }

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
