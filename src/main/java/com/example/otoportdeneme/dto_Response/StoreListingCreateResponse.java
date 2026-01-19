package com.example.otoportdeneme.dto_Response;

import com.example.otoportdeneme.dto_Objects.StoreListingCreatedDto;

public class StoreListingCreateResponse {

    private final boolean ok;
    private final StoreListingCreatedDto listing;

    public StoreListingCreateResponse(StoreListingCreatedDto listing) {
        this.ok = true;
        this.listing = listing;
    }

    public boolean isOk() { return ok; }
    public StoreListingCreatedDto getListing() { return listing; }
}
