package com.example.otoportdeneme.dto_Response;

import com.example.otoportdeneme.dto_Objects.ListingDetailDto;

public class ListingDetailResponse {

    private ListingDetailDto listing;

    public ListingDetailResponse() {}

    public ListingDetailResponse(ListingDetailDto listing) {
        this.listing = listing;
    }

    public ListingDetailDto getListing() { return listing; }
    public void setListing(ListingDetailDto listing) { this.listing = listing; }
}
