package com.example.otoportdeneme.controllers;

import com.example.otoportdeneme.dto_Objects.ListingCardDto;
import com.example.otoportdeneme.dto_Objects.ListingMapper;
import com.example.otoportdeneme.dto_Response.ListingDetailResponse;
import com.example.otoportdeneme.repositories.ListingRepository;
import com.example.otoportdeneme.services.ListingDetailService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/listings")
public class ListingController {

    private final ListingDetailService listingDetailService;
    private final ListingRepository listingRepository;

    public ListingController(ListingDetailService listingDetailService,
                             ListingRepository listingRepository) {
        this.listingDetailService = listingDetailService;
        this.listingRepository = listingRepository;
    }

    // ✅ /api/listings
    @GetMapping
    public List<ListingCardDto> getListings() {
        return listingRepository.findAllForCard()
                .stream()
                .map(ListingMapper::toCardDto)
                .toList();
    }

    // ✅ Detay: GET /api/listings/{listingId}
    @GetMapping("/{listingId}")
    public ListingDetailResponse getListingDetail(
            @PathVariable Long listingId,
            @RequestParam(required = false) Long clientId
    ) {
        return new ListingDetailResponse(
                listingDetailService.getListingDetail(listingId, clientId)
        );
    }
}
