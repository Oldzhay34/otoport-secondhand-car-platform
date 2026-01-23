package com.example.otoportdeneme.controllers;

import com.example.otoportdeneme.dto_Requests.AdminSendNotificationRequest;
import com.example.otoportdeneme.dto_Objects.StoreOptionDto;
import com.example.otoportdeneme.repositories.StoreRepository;
import com.example.otoportdeneme.services.AdminNotificationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/notifications")
public class AdminSendNotificationController {

    private final AdminNotificationService adminNotificationService;
    private final StoreRepository storeRepository;

    public AdminSendNotificationController(AdminNotificationService adminNotificationService,
                                       StoreRepository storeRepository) {
        this.adminNotificationService = adminNotificationService;
        this.storeRepository = storeRepository;
    }

    // Dropdown için store listesi
    @GetMapping("/stores")
    @PreAuthorize("hasRole('ADMIN')")
    public List<StoreOptionDto> stores() {
        return storeRepository.findAll().stream()
                .map(s -> new StoreOptionDto(
                        s.getId(),
                        // ⚠️ burayı kendi Store alanlarına göre düzelt
                        s.getStoreName(),
                        s.getCity(),
                        s.getDistrict()
                ))
                .toList();
    }

    // Tek store'a gönder
    @PostMapping("/send-to-store")
    @PreAuthorize("hasRole('ADMIN')")
    public void sendToStore(@RequestBody AdminSendNotificationRequest req) {
        adminNotificationService.sendToStore(
                req.getStoreId(),
                req.getType(),
                req.getTitle(),
                req.getMessage(),
                req.getPayloadJson()
        );
    }

    // Tüm store'lara gönder
    @PostMapping("/broadcast-to-stores")
    @PreAuthorize("hasRole('ADMIN')")
    public void broadcast(@RequestBody AdminSendNotificationRequest req) {
        adminNotificationService.broadcastToAllStores(
                req.getType(),
                req.getTitle(),
                req.getMessage(),
                req.getPayloadJson()
        );
    }
}
