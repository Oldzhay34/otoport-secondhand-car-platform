package com.example.otoportdeneme.dto_Response;

import com.example.otoportdeneme.dto_Objects.InquiryMessageDto;

import java.time.Instant;
import java.util.List;

public class StoreInquiryThreadResponse {
    private Long inquiryId;
    private Long listingId;
    private String listingTitle;

    private String status;
    private Instant createdAt;

    private String clientName;
    private String clientEmail;

    private String guestName;
    private String guestEmail;
    private String guestPhone;

    private List<InquiryMessageDto> messages;

    public StoreInquiryThreadResponse() {}

    public Long getInquiryId() { return inquiryId; }
    public void setInquiryId(Long inquiryId) { this.inquiryId = inquiryId; }

    public Long getListingId() { return listingId; }
    public void setListingId(Long listingId) { this.listingId = listingId; }

    public String getListingTitle() { return listingTitle; }
    public void setListingTitle(String listingTitle) { this.listingTitle = listingTitle; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getClientEmail() { return clientEmail; }
    public void setClientEmail(String clientEmail) { this.clientEmail = clientEmail; }

    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }

    public String getGuestEmail() { return guestEmail; }
    public void setGuestEmail(String guestEmail) { this.guestEmail = guestEmail; }

    public String getGuestPhone() { return guestPhone; }
    public void setGuestPhone(String guestPhone) { this.guestPhone = guestPhone; }

    public List<InquiryMessageDto> getMessages() { return messages; }
    public void setMessages(List<InquiryMessageDto> messages) { this.messages = messages; }
}
