package com.example.otoportdeneme.services;

import com.example.otoportdeneme.models.Listing;

import java.util.List;

public interface ListingService {
    Listing createListing(Long storeId, Listing listing, List<String> imagePaths);
}
