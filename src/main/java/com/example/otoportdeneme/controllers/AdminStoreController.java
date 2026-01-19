package com.example.otoportdeneme.controllers;

import com.example.otoportdeneme.dto_Requests.StoreCreateRequest;
import com.example.otoportdeneme.models.Store;
import com.example.otoportdeneme.services.StoreAdminService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/stores")
public class AdminStoreController {

    private final StoreAdminService storeAdminService;

    public AdminStoreController(StoreAdminService storeAdminService) {
        this.storeAdminService = storeAdminService;
    }

    @PostMapping
    public Map<String, Object> create(@Valid @RequestBody StoreCreateRequest req) {

        Store s = storeAdminService.createStoreWithPassword(
                req.getEmail(),
                req.getPassword(),
                req.getStoreName(),
                req.getCity(),
                req.getDistrict(),
                req.getPhone()
        );

        return Map.of(
                "id", s.getId(),
                "email", s.getEmail(),
                "storeName", s.getStoreName()
        );
    }
}
