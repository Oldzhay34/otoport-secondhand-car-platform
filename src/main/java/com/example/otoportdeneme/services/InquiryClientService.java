package com.example.otoportdeneme.services;

import com.example.otoportdeneme.dto_Response.InquiryUpsertResponse;

public interface InquiryClientService {
    InquiryUpsertResponse upsert(Long clientId, Long listingId, String message, String ip, String ua);
    InquiryUpsertResponse getThread(Long clientId, Long listingId);
}
