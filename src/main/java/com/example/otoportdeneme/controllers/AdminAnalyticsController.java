package com.example.otoportdeneme.controllers;

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
    private static final ZoneId TR = ZoneId.of("Europe/Istanbul");

    public AdminAnalyticsController(AdminAnalyticsService svc) {
        this.svc = svc;
    }

    @GetMapping("/daily")
    public AdminDailyStatsDto daily(@RequestParam(required = false) String date) {
        LocalDate d = (date == null || date.isBlank()) ? LocalDate.now(TR) : LocalDate.parse(date);
        return svc.daily(d);
    }

    @GetMapping("/hourly")
    public List<HourlyPointDto> hourly(@RequestParam(required = false) String date) {
        LocalDate d = (date == null || date.isBlank()) ? LocalDate.now(TR) : LocalDate.parse(date);
        return svc.hourly(d);
    }

    @GetMapping("/store-activity")
    public List<StoreActivityDto> storeActivity(@RequestParam(required = false) String date) {
        LocalDate d = (date == null || date.isBlank()) ? LocalDate.now(TR) : LocalDate.parse(date);
        return svc.storeActivity(d);
    }


}
