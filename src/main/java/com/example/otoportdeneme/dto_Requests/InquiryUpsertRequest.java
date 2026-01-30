package com.example.otoportdeneme.dto_Requests;

public class InquiryUpsertRequest {
    private Long listingId;
    private Long storeId;
    private String message;

    // guest alanları (token yoksa)
    private String guestName;
    private String guestEmail;
    private String guestPhone;

    public Long getListingId() { return listingId; }
    public void setListingId(Long listingId) { this.listingId = listingId; }

    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }

    public String getGuestEmail() { return guestEmail; }
    public void setGuestEmail(String guestEmail) { this.guestEmail = guestEmail; }

    public String getGuestPhone() { return guestPhone; }
    public void setGuestPhone(String guestPhone) { this.guestPhone = guestPhone; }
}
