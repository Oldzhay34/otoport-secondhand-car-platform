package com.example.otoportdeneme.dto_Response;

import java.util.Map;

public class AdminDailyStatsDto {
    public String date;                // "2026-01-19"
    public long guestAccessCount;       // GuestAccessLog
    public Map<String, Long> loginByRole; // CLIENT/STORE/ADMIN -> count
    public long totalLoginCount;

    public AdminDailyStatsDto(String date, long guestAccessCount, Map<String, Long> loginByRole, long totalLoginCount) {
        this.date = date;
        this.guestAccessCount = guestAccessCount;
        this.loginByRole = loginByRole;
        this.totalLoginCount = totalLoginCount;
    }
}
