package com.example.otoportdeneme.services;

import com.example.otoportdeneme.dto_Requests.StoreListingCreateRequest;
import com.example.otoportdeneme.models.Listing;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StoreCreateListingService {

    /**
     * Store kendi ilanını oluşturur.
     * images opsiyonel (null olabilir) ama max 10.
     */
    Listing createMyListing(Long storeId, StoreListingCreateRequest req, List<MultipartFile> images);
}
