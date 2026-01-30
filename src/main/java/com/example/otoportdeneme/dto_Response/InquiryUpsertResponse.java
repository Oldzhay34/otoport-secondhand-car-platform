package com.example.otoportdeneme.dto_Response;

import com.example.otoportdeneme.dto_Objects.InquiryMessageDto;

import java.time.Instant;
import java.util.List;

public class InquiryUpsertResponse {
    private Long inquiryId;
    private Long listingId;
    private Long storeId;

    private String status;
    private Instant createdAt;

    private String guestName;
    private String guestEmail;
    private String guestPhone;

    private String clientEmail; // login client varsa
    private List<InquiryMessageDto> messages;

    public Long getInquiryId() { return inquiryId; }
    public void setInquiryId(Long inquiryId) { this.inquiryId = inquiryId; }

    public Long getListingId() { return listingId; }
    public void setListingId(Long listingId) { this.listingId = listingId; }

    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }

    public String getGuestEmail() { return guestEmail; }
    public void setGuestEmail(String guestEmail) { this.guestEmail = guestEmail; }

    public String getGuestPhone() { return guestPhone; }
    public void setGuestPhone(String guestPhone) { this.guestPhone = guestPhone; }

    public String getClientEmail() { return clientEmail; }
    public void setClientEmail(String clientEmail) { this.clientEmail = clientEmail; }

    public List<InquiryMessageDto> getMessages() { return messages; }
    public void setMessages(List<InquiryMessageDto> messages) { this.messages = messages; }
}
