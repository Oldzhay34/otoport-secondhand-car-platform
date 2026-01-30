package com.example.otoportdeneme.controllers;

import com.example.otoportdeneme.dto_Response.StoreListingDetailResponse;
import com.example.otoportdeneme.repositories.StoreRepository;
import com.example.otoportdeneme.services.StoreListingDetailService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/store/listings")
public class StoreListingDetailController {

    private final StoreListingDetailService service;
    private final StoreRepository storeRepository;

    public StoreListingDetailController(StoreListingDetailService service, StoreRepository storeRepository) {
        this.service = service;
        this.storeRepository = storeRepository;
    }

    @GetMapping("/{id}/detail")
    public StoreListingDetailResponse getMyListingDetail(Authentication auth, @PathVariable("id") Long listingId) {
        Long storeId = resolveStoreId(auth);
        return service.getMyListingDetail(storeId, listingId);
    }

    private Long resolveStoreId(Authentication auth) {
        if (auth == null) throw new IllegalArgumentException("Unauthorized");
        String email = auth.getName();
        return storeRepository.findIdByEmail(email).orElseThrow(() -> new IllegalArgumentException("Store not found"));
    }
}
