package com.example.otoportdeneme.services;

import com.example.otoportdeneme.Interfaces.StoreCardView;

import java.util.List;

public interface StoreService {
    List<StoreCardView> getHomepageStores(int limit);
}
