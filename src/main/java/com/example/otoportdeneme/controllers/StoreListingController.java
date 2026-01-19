package com.example.otoportdeneme.controllers;

import com.example.otoportdeneme.dto_Objects.ListingCardDto;
import com.example.otoportdeneme.dto_Objects.ListingMapper;
import com.example.otoportdeneme.dto_Requests.ListingFilterRequest;
import com.example.otoportdeneme.dto_Response.StoreListingsResponse;
import com.example.otoportdeneme.models.Listing;
import com.example.otoportdeneme.services.StoreListingFilterService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stores")
public class StoreListingController {

    private final StoreListingFilterService filterService;

    public StoreListingController(StoreListingFilterService filterService) {
        this.filterService = filterService;
    }

    @PostMapping("/{storeId}/listings/filter")
    public StoreListingsResponse filterStoreListings(
            @PathVariable Long storeId,
            @Valid @RequestBody ListingFilterRequest req
    ) {
        List<Listing> listings = filterService.filterStoreListings(
                storeId,
                req.getBodyType(),
                req.getFeatureIds(),
                req.getMatchMode(),
                req.getFloor(),
                req.getLimit()
        );

        List<ListingCardDto> cards = listings.stream()
                .map(ListingMapper::toCardDto)
                .collect(Collectors.toList());

        return new StoreListingsResponse(storeId, cards);
    }
}
