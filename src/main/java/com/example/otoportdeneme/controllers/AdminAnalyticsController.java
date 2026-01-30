package com.example.otoportdeneme.controllers;

import com.example.otoportdeneme.dto_Objects.admin.SpamAttemptActorDto;
import com.example.otoportdeneme.dto_Response.AdminDailyStatsDto;
import com.example.otoportdeneme.dto_Response.HourlyPointDto;
import com.example.otoportdeneme.dto_Response.StoreActivityDto;
import com.example.otoportdeneme.services.AdminAnalyticsService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@RestController
@RequestMapping("/api/admin/analytics")
public class AdminAnalyticsController {

    private final AdminAnalyticsService svc;

    public AdminAnalyticsController(AdminAnalyticsService svc) {
        this.svc = svc;
    }

    @GetMapping("/daily")
    public AdminDailyStatsDto daily(@RequestParam(required = false) String date) {
        ZoneId tr = ZoneId.of("Europe/Istanbul");
        LocalDate d = (date == null || date.isBlank()) ? LocalDate.now(tr) : LocalDate.parse(date);
        return svc.daily(d);
    }

    @GetMapping("/hourly")
    public List<HourlyPointDto> hourly(@RequestParam(required = false) String date) {
        ZoneId tr = ZoneId.of("Europe/Istanbul");
        LocalDate d = (date == null || date.isBlank()) ? LocalDate.now(tr) : LocalDate.parse(date);
        return svc.hourly(d);
    }

    @GetMapping("/store-activity")
    public List<StoreActivityDto> storeActivity(@RequestParam(required = false) String date) {
        ZoneId tr = ZoneId.of("Europe/Istanbul");
        LocalDate d = (date == null || date.isBlank()) ? LocalDate.now(tr) : LocalDate.parse(date);
        return svc.storeActivity(d);
    }
    @GetMapping("/api/admin/dashboard/spam-attempts")
    public List<SpamAttemptActorDto> spamAttempts(@RequestParam String date) {
        LocalDate d = LocalDate.parse(date);
        return svc.spamAttemptActors(d);
    }


}
