package com.example.otoportdeneme.controllers;

import com.example.otoportdeneme.Interfaces.StoreCardView;
import com.example.otoportdeneme.models.GuestAccessLog;
import com.example.otoportdeneme.services.StoreService;
import com.example.otoportdeneme.dto_Objects.StoreCardDto;
import com.example.otoportdeneme.dto_Objects.StoreMapper;
import com.example.otoportdeneme.dto_Response.HomeStoresResponse;
import com.example.otoportdeneme.repositories.GuestAccessLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/home")
public class HomeController {

    private final StoreService storeService;
    private final GuestAccessLogRepository guestAccessLogRepository;

    public HomeController(StoreService storeService,
                          GuestAccessLogRepository guestAccessLogRepository) {
        this.storeService = storeService;
        this.guestAccessLogRepository = guestAccessLogRepository;
    }

    @GetMapping("/stores")
    public HomeStoresResponse getStores(@RequestParam(defaultValue = "50") int limit) {
        var stores = storeService.getHomepageStores(limit);
        var dtos = stores.stream().map(StoreMapper::toDto).toList();
        return new HomeStoresResponse(dtos);
    }

    // ✅ DOĞRU PATH
    @PostMapping("/guest-hit")
    public Map<String,Object> guestHit(HttpServletRequest req){
        GuestAccessLog g = new GuestAccessLog();
        g.setAccessType("HOME");
        g.setTarget("/templates/home.html");
        g.setIpAddress(resolveClientIp(req));
        g.setUserAgent(req.getHeader("User-Agent"));

        guestAccessLogRepository.save(g);
        return Map.of("ok", true);
    }

    private String resolveClientIp(HttpServletRequest req) {
        String xff = req.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) return xff.split(",")[0].trim();
        return req.getRemoteAddr();
    }
}
