package com.example.otoportdeneme.dto_Response;

import com.example.otoportdeneme.dto_Objects.ListingCardDto;

public class ListingUpdateResponse {
    private ListingCardDto listing;

    public ListingUpdateResponse() {}

    public ListingUpdateResponse(ListingCardDto listing) {
        this.listing = listing;
    }

    public ListingCardDto getListing() { return listing; }
    public void setListing(ListingCardDto listing) { this.listing = listing; }
}
