package com.example.otoportdeneme.services;

import com.example.otoportdeneme.dto_Objects.admin.SpamAttemptActorDto;
import com.example.otoportdeneme.dto_Response.AdminDailyStatsDto;
import com.example.otoportdeneme.dto_Response.HourlyPointDto;
import com.example.otoportdeneme.dto_Response.StoreActivityDto;

import java.time.LocalDate;
import java.util.List;

public interface AdminAnalyticsService {
    AdminDailyStatsDto daily(LocalDate date);
    List<HourlyPointDto> hourly(LocalDate date);
    List<StoreActivityDto> storeActivity(LocalDate date);
    List<SpamAttemptActorDto> spamAttemptActors(LocalDate date);
}
