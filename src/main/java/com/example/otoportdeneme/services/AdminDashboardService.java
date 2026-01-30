package com.example.otoportdeneme.services;

import com.example.otoportdeneme.dto_Objects.admin.*;

import java.time.LocalDate;
import java.util.List;

public interface AdminDashboardService {
    DailyVisitStatsDto getDailyVisits(LocalDate date);
    HourlyTrafficDto getHourlyTraffic(LocalDate date);

    List<StoreListingActivityDto> getStoreListingActivity(LocalDate date);

    // istersen son hareketler (audit feed)
    List<AuditRowDto> getRecentAudit(LocalDate date, int limit);

    List<SpamAttemptActorDto> getSpamAttemptActors(LocalDate d);
}
