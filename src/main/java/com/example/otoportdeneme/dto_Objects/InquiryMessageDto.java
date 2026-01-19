package com.example.otoportdeneme.dto_Objects;

import java.time.Instant;

public class InquiryMessageDto {
    private Long id;
    private String senderType; // STORE / CLIENT
    private String content;
    private Instant sentAt;

    public InquiryMessageDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSenderType() { return senderType; }
    public void setSenderType(String senderType) { this.senderType = senderType; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Instant getSentAt() { return sentAt; }
    public void setSentAt(Instant sentAt) { this.sentAt = sentAt; }
}
