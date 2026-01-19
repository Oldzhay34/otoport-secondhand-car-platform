package com.example.otoportdeneme.controllers;

import com.example.otoportdeneme.dto_Objects.StorePublicDto;
import com.example.otoportdeneme.models.Store;
import com.example.otoportdeneme.repositories.StoreRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stores")
public class StorePublicController {

    private final StoreRepository storeRepository;

    public StorePublicController(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    @GetMapping("/{storeId}")
    public StorePublicDto getStore(@PathVariable Long storeId) {
        Store s = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Store not found"));

        StorePublicDto dto = new StorePublicDto();
        dto.setId(s.getId());
        dto.setStoreName(s.getStoreName());
        dto.setVerified(s.getVerified());

        dto.setCity(s.getCity());
        dto.setDistrict(s.getDistrict());
        dto.setAddressLine(s.getAddressLine());

        dto.setPhone(s.getPhone());
        dto.setWebsite(s.getWebsite());

        dto.setFloor(s.getFloor());
        dto.setShopNo(s.getShopNo());
        dto.setDirectionNote(s.getDirectionNote());
        return dto;
    }
}
