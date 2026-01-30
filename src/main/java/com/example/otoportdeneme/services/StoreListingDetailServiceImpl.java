package com.example.otoportdeneme.services;

import com.example.otoportdeneme.dto_Objects.StoreListingDetailMapper;
import com.example.otoportdeneme.dto_Response.StoreListingDetailResponse;
import com.example.otoportdeneme.models.Listing;
import com.example.otoportdeneme.repositories.ListingRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class StoreListingDetailServiceImpl implements StoreListingDetailService {

    private final ListingRepository listingRepository;

    public StoreListingDetailServiceImpl(ListingRepository listingRepository) {
        this.listingRepository = listingRepository;
    }

    @Override
    @Transactional
    public StoreListingDetailResponse getMyListingDetail(Long storeId, Long listingId) {

        Listing listing = listingRepository.findStoreDetailByIdFetchAll(listingId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing not found"));

        if (listing.getStore() == null || listing.getStore().getId() == null || !listing.getStore().getId().equals(storeId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }

        return StoreListingDetailMapper.toResponse(listing);
    }
}
