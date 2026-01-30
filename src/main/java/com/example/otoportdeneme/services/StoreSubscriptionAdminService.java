package com.example.otoportdeneme.services;

import com.example.otoportdeneme.dto_Requests.StoreSubscriptionAdminRowDto;

import java.util.List;

public interface StoreSubscriptionAdminService {
    List<StoreSubscriptionAdminRowDto> list();
    void setPlan(Long storeId, String plan);
}
