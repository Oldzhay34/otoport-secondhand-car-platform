package com.example.otoportdeneme.services;

import com.example.otoportdeneme.dto_Response.InquiryUpsertResponse;
import com.example.otoportdeneme.dto_Requests.InquiryUpsertRequest;

public interface InquiryPublicService {
    InquiryUpsertResponse upsert(InquiryUpsertRequest req, String authEmailOrNull, String ip, String userAgent);
    InquiryUpsertResponse getThread(Long inquiryId, String authEmailOrNull);
    InquiryUpsertResponse getThreadByListing(Long listingId, String authEmailOrNull);
    InquiryUpsertResponse reply(Long inquiryId, String message, String authEmailOrNull, String guestEmailOrNull, String ip, String userAgent);
}
