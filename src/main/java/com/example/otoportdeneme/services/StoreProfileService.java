package com.example.otoportdeneme.services;

import com.example.otoportdeneme.Interfaces.StoreProfileView;

public interface StoreProfileService {

    StoreProfileView getStoreProfileOrThrow(Long storeId);

    StoreProfileView updateStoreProfile(
            Long storeId,
            String storeName,
            String authorizedPerson,
            String taxNo,
            String website,
            String city,
            String district,
            String addressLine,
            Integer floor,         // 1..8
            String shopNo,
            String directionNote,
            String phone
    );
}
