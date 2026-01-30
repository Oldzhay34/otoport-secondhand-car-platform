package com.example.otoportdeneme.controllers;

import com.example.otoportdeneme.dto_Objects.admin.*;
import com.example.otoportdeneme.services.AdminDashboardService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;

@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

    private final AdminDashboardService service;
    private static final ZoneId TR = ZoneId.of("Europe/Istanbul");

    public AdminDashboardController(AdminDashboardService service) {
        this.service = service;
    }

    // ?date=2026-01-19 (opsiyonel)
    @GetMapping("/daily")
    public DailyVisitStatsDto daily(@RequestParam(required = false) String date) {
        LocalDate d = (date == null || date.isBlank()) ? LocalDate.now(TR) : LocalDate.parse(date);
        return service.getDailyVisits(d);
    }

    @GetMapping("/hourly")
    public HourlyTrafficDto hourly(@RequestParam(required = false) String date) {
        LocalDate d = (date == null || date.isBlank()) ? LocalDate.now(TR) : LocalDate.parse(date);
        return service.getHourlyTraffic(d);
    }

    @GetMapping("/stores/listing-activity")
    public java.util.List<StoreListingActivityDto> storeListingActivity(@RequestParam(required = false) String date) {
        LocalDate d = (date == null || date.isBlank()) ? LocalDate.now(TR) : LocalDate.parse(date);
        return service.getStoreListingActivity(d);
    }

    @GetMapping("/audit")
    public java.util.List<AuditRowDto> audit(@RequestParam(required = false) String date,
                                             @RequestParam(defaultValue = "50") int limit) {
        LocalDate d = (date == null || date.isBlank()) ? LocalDate.now(TR) : LocalDate.parse(date);
        return service.getRecentAudit(d, limit);
    }

    /**
     * ✅ Admin panelin çağırdığı endpoint:
     * /api/admin/dashboard/spam-attempts?date=YYYY-MM-DD
     */
    @GetMapping("/spam-attempts")
    public java.util.List<SpamAttemptActorDto> spamAttempts(@RequestParam(required = false) String date) {
        LocalDate d = (date == null || date.isBlank()) ? LocalDate.now(TR) : LocalDate.parse(date);
        return service.getSpamAttemptActors(d);
    }
}
