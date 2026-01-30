package com.example.otoportdeneme.services;

import com.example.otoportdeneme.dto_Objects.ListingDetailDto;
import com.example.otoportdeneme.dto_Objects.StoreListingEditDto;
import com.example.otoportdeneme.dto_Requests.StoreCarUpdateRequest;

public interface StoreListingService {

    StoreListingEditDto getMyListingForEdit(Long storeId, Long listingId);

    StoreListingEditDto updateMyListing(Long storeId, Long listingId, StoreCarUpdateRequest req);

    void deleteMyListing(Long storeId, Long listingId);

    ListingDetailDto getMyListingDetail(Long storeId, Long listingId);
}
