package com.example.otoportdeneme.controllers;

import com.example.otoportdeneme.dto_Requests.StoreSubscriptionAdminRowDto;
import com.example.otoportdeneme.dto_Requests.UpdateStorePlanRequest;
import com.example.otoportdeneme.services.StoreSubscriptionAdminService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/store-subscriptions")
public class AdminStoreSubscriptionController {

    private final StoreSubscriptionAdminService service;

    public AdminStoreSubscriptionController(StoreSubscriptionAdminService service) {
        this.service = service;
    }

    @GetMapping("/stores")
    public List<StoreSubscriptionAdminRowDto> list() {
        return service.list();
    }

    @PatchMapping("/{storeId}/plan")
    public Map<String, Object> setPlan(@PathVariable Long storeId,
                                       @Valid @RequestBody UpdateStorePlanRequest req) {
        service.setPlan(storeId, req.getPlan());
        return Map.of("ok", true);
    }
}
