package com.example.otoportdeneme.services;

public interface GuestLogService {
    void log(String accessType, String target, String ip, String userAgent);
}
