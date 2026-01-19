package com.example.otoportdeneme.services;

import com.example.otoportdeneme.dto_Response.ListingCardResponse;
import com.example.otoportdeneme.dto_Objects.ListingDetailDto;

import java.util.List;

public interface ListingDetailService {
    ListingDetailDto getListingDetail(Long listingId, Long clientIdNullable);

    List<ListingCardResponse> filterListings(
            Long storeId,
            String brand,
            String model,
            String engine,
            String pack,
            Integer yearMin,
            Integer yearMax
    );
}
