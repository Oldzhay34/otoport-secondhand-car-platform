package com.example.otoportdeneme.controllers;

import com.example.otoportdeneme.Interfaces.StoreProfileView;
import com.example.otoportdeneme.services.StoreProfileService;
import com.example.otoportdeneme.dto_Objects.StoreProfileDto;
import com.example.otoportdeneme.dto_Objects.StoreProfileMapper;
import com.example.otoportdeneme.dto_Requests.StoreProfileUpdateRequest;
import com.example.otoportdeneme.dto_Response.StoreProfileResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/store/profile")
public class StoreProfileController {

    private final StoreProfileService storeProfileService;

    public StoreProfileController(StoreProfileService storeProfileService) {
        this.storeProfileService = storeProfileService;
    }

    // ✅ Profil görüntüleme
    @GetMapping
    public StoreProfileResponse getProfile(@RequestParam Long storeId) {

        StoreProfileView view =
                storeProfileService.getStoreProfileOrThrow(storeId);

        StoreProfileDto dto = StoreProfileMapper.toDto(view);
        return new StoreProfileResponse(dto);
    }

    // ✅ Profil güncelleme (DTO)
    @PutMapping
    public StoreProfileResponse updateProfile(
            @RequestParam Long storeId,
            @Valid @RequestBody StoreProfileUpdateRequest req
    ) {
        StoreProfileView updated =
                storeProfileService.updateStoreProfile(
                        storeId,
                        req.getStoreName(),
                        req.getAuthorizedPerson(),
                        null, // taxNo şimdilik admin tarafında
                        req.getWebsite(),
                        req.getCity(),
                        req.getDistrict(),
                        req.getAddressLine(),
                        req.getFloor(),
                        req.getShopNo(),
                        req.getDirectionNote(),
                        req.getPhone()
                );

        return new StoreProfileResponse(
                StoreProfileMapper.toDto(updated)
        );
    }
}
