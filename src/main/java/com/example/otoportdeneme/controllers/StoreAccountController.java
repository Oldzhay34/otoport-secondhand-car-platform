package com.example.otoportdeneme.controllers;

import com.example.otoportdeneme.dto_Requests.StoreChangePasswordRequest;
import com.example.otoportdeneme.dto_Response.LogoUploadResponse;
import com.example.otoportdeneme.repositories.StoreRepository;
import com.example.otoportdeneme.services.StoreAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/store/me")
public class StoreAccountController {

    private final StoreAccountService storeAccountService;
    private final StoreRepository storeRepository;

    public StoreAccountController(StoreAccountService storeAccountService,
                                  StoreRepository storeRepository) {
        this.storeAccountService = storeAccountService;
        this.storeRepository = storeRepository;
    }

    @PutMapping("/password")
    public void changePassword(Authentication auth,
                               @RequestBody StoreChangePasswordRequest req) {
        Long storeId = resolveStoreId(auth);
        storeAccountService.changePassword(storeId, req);
    }

    @PostMapping(value = "/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public LogoUploadResponse uploadLogo(Authentication auth,
                                         @RequestPart("file") MultipartFile file) {
        Long storeId = resolveStoreId(auth);
        String url = storeAccountService.updateLogo(storeId, file);
        return new LogoUploadResponse(url);
    }

    private Long resolveStoreId(Authentication auth) {
        if (auth == null || auth.getName() == null || auth.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        return storeRepository.findIdByEmail(auth.getName().trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));
    }
}
