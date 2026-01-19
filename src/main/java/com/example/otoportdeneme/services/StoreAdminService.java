package com.example.otoportdeneme.services;

import com.example.otoportdeneme.models.Store;

public interface StoreAdminService {
    Store createStoreWithPassword(
            String email,
            String rawPassword,
            String storeName,
            String city,
            String district,
            String phone
    );
}
