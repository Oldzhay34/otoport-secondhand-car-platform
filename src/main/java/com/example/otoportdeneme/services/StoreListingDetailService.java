package com.example.otoportdeneme.services;

import com.example.otoportdeneme.dto_Response.StoreListingDetailResponse;

public interface StoreListingDetailService {
    StoreListingDetailResponse getMyListingDetail(Long storeId, Long listingId);
}
