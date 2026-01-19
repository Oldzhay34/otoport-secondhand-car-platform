package com.example.otoportdeneme.services;

public interface InquiryService {

    Long createInquiryGuest(
            Long listingId,
            String guestName,
            String guestEmail,
            String guestPhone,
            String firstMessage,
            String ip,
            String userAgent
    );

    Long createInquiryClient(
            Long listingId,
            Long clientId,
            String firstMessage,
            String ip,
            String userAgent
    );
}
