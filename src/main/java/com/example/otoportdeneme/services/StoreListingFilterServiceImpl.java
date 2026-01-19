package com.example.otoportdeneme.services;

import com.example.otoportdeneme.Enums.BodyType;
import com.example.otoportdeneme.Enums.FeatureMatchMode;
import com.example.otoportdeneme.Enums.ListingStatus;
import com.example.otoportdeneme.models.Listing;
import com.example.otoportdeneme.repositories.ListingRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StoreListingFilterServiceImpl implements StoreListingFilterService {

    private final ListingRepository listingRepository;

    public StoreListingFilterServiceImpl(ListingRepository listingRepository) {
        this.listingRepository = listingRepository;
    }

    @Override
    @Transactional
    public List<Listing> filterStoreListings(
            Long storeId,
            BodyType bodyType,
            List<Long> featureIds,
            FeatureMatchMode matchMode,
            Integer floor,
            int limit
    ) {
        if (storeId == null) throw new IllegalArgumentException("storeId is required");
        if (limit <= 0) limit = 50;

        // ✅ LAZY HATASINI BİTİREN YER: FETCH JOIN
        List<Listing> result = listingRepository.findByStoreIdAndStatusFetchAll(
                storeId, ListingStatus.ACTIVE
        );

        // bodyType basit filtre
        if (bodyType != null) {
            result = result.stream()
                    .filter(l -> l.getCar() != null && l.getCar().getBodyType() == bodyType)
                    .collect(Collectors.toList());
        }

        if (result.size() > limit) return result.subList(0, limit);
        return result;
    }
}
