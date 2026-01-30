package com.example.otoportdeneme.services;

public interface InquiryMessageService {

    void replyAsStore(
            Long inquiryId,
            Long storeId,
            String message,
            String ip,
            String userAgent
    );

    void replyAsClient(
            Long inquiryId,
            Long clientId,
            String message,
            String ip,
            String userAgent
    );

    void markReadByStore(Long inquiryId);

    void markReadByClient(Long inquiryId);

}
