package com.example.otoportdeneme.controllers;

import com.example.otoportdeneme.dto_Objects.ListingCardDto;
import com.example.otoportdeneme.dto_Objects.ListingDetailDto;
import com.example.otoportdeneme.dto_Objects.ListingMapper;
import com.example.otoportdeneme.dto_Requests.ListingFilterRequest;
import com.example.otoportdeneme.dto_Response.StoreListingsResponse;
import com.example.otoportdeneme.models.Listing;
import com.example.otoportdeneme.repositories.StoreRepository;
import com.example.otoportdeneme.services.StoreListingFilterService;
import com.example.otoportdeneme.services.StoreListingService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stores")
public class StoreListingController {

    private final StoreListingFilterService filterService;
    private final StoreListingService storeListingService;
    private final StoreRepository storeRepository;

    public StoreListingController(StoreListingFilterService filterService, StoreListingService storeListingService, StoreRepository storeRepository) {
        this.filterService = filterService;
        this.storeListingService = storeListingService;
        this.storeRepository = storeRepository;

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
    @GetMapping("/{listingId}/detail")
    public ListingDetailDto getMyListingDetail(Authentication auth,
                                               @PathVariable Long listingId) {

        Long storeId = resolveStoreId(auth);
        return storeListingService.getMyListingDetail(storeId, listingId);
    }

    private Long resolveStoreId(Authentication auth) {
        if (auth == null) throw new IllegalArgumentException("Unauthorized");
        String email = auth.getName();
        return storeRepository.findIdByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Store not found"));
    }
}
