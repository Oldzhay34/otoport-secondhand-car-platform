package com.example.otoportdeneme.services;

import com.example.otoportdeneme.dto_Requests.StoreChangePasswordRequest;
import org.springframework.web.multipart.MultipartFile;

public interface StoreAccountService {
    void changePassword(Long storeId, StoreChangePasswordRequest req);
    String updateLogo(Long storeId, MultipartFile file);
}
