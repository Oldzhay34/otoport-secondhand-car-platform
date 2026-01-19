package com.example.otoportdeneme.services;

import com.example.otoportdeneme.Enums.ListingStatus;
import com.example.otoportdeneme.repositories.StoreSubscriptionRepository;
import com.example.otoportdeneme.repositories.ListingRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    private final StoreSubscriptionRepository subscriptionRepository;
    private final ListingRepository listingRepository;

    public SubscriptionServiceImpl(StoreSubscriptionRepository subscriptionRepository,
                                   ListingRepository listingRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.listingRepository = listingRepository;
    }

    @Override
    @Transactional
    public void assertStoreCanCreateListing(Long storeId) {
        var sub = subscriptionRepository.findByStoreId(storeId)
                .orElseThrow(() -> new IllegalStateException("Store subscription not found."));

        if (!Boolean.TRUE.equals(sub.getIsActive())) {
            throw new IllegalStateException("Subscription is not active.");
        }
        if (sub.getEndsAt() != null && sub.getEndsAt().isBefore(Instant.now())) {
            throw new IllegalStateException("Subscription expired.");
        }


        long count = listingRepository.countByStoreIdAndStatus(storeId, ListingStatus.ACTIVE);
        if (count >= sub.getListingLimit()) throw new IllegalStateException("Listing limit reached.");
    }
}
