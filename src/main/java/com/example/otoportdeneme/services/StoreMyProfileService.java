package com.example.otoportdeneme.services;

import com.example.otoportdeneme.dto_Objects.StoreMyProfileDto;
import com.example.otoportdeneme.dto_Requests.StoreMyProfileUpdateRequest;

public interface StoreMyProfileService {
    StoreMyProfileDto getMyProfile(Long storeId);
    StoreMyProfileDto updateMyProfile(Long storeId, StoreMyProfileUpdateRequest req);
}
