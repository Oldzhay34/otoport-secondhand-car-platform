package com.example.otoportdeneme.controllers;

import com.example.otoportdeneme.dto_Objects.StoreMyProfileDto;
import com.example.otoportdeneme.dto_Requests.StoreMyProfileUpdateRequest;
import com.example.otoportdeneme.repositories.StoreRepository;
import com.example.otoportdeneme.services.StoreMyProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/store/me")
public class StoreMyProfileController {

    private final StoreMyProfileService storeMyProfileService;
    private final StoreRepository storeRepository;

    public StoreMyProfileController(StoreMyProfileService storeMyProfileService,
                                    StoreRepository storeRepository) {
        this.storeMyProfileService = storeMyProfileService;
        this.storeRepository = storeRepository;
    }

    @GetMapping("/profile")
    public StoreMyProfileDto me(Authentication auth) {
        Long storeId = resolveStoreId(auth);
        return storeMyProfileService.getMyProfile(storeId);
    }

    @PutMapping("/profile")
    public StoreMyProfileDto update(Authentication auth,
                                    @RequestBody StoreMyProfileUpdateRequest req) {
        Long storeId = resolveStoreId(auth);
        return storeMyProfileService.updateMyProfile(storeId, req);
    }

    private Long resolveStoreId(Authentication auth) {
        if (auth == null || auth.getName() == null || auth.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        return storeRepository.findIdByEmail(auth.getName().trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));
    }
}
