package com.example.otoportdeneme.dto_Response;

import com.example.otoportdeneme.dto_Objects.ListingCardDto;
import java.util.List;

public class StoreListingsResponse {
    private Long storeId;
    private List<ListingCardDto> listings;

    public StoreListingsResponse() {}

    public StoreListingsResponse(Long storeId, List<ListingCardDto> listings) {
        this.storeId = storeId;
        this.listings = listings;
    }

    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }

    public List<ListingCardDto> getListings() { return listings; }
    public void setListings(List<ListingCardDto> listings) { this.listings = listings; }
}
