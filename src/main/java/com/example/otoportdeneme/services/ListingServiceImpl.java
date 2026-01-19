package com.example.otoportdeneme.services;

import com.example.otoportdeneme.repositories.ListingRepository;
import com.example.otoportdeneme.repositories.StoreRepository;
import jakarta.transaction.Transactional;
import com.example.otoportdeneme.models.Listing;
import com.example.otoportdeneme.models.ListingImage;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class ListingServiceImpl implements ListingService {

    private final ListingRepository listingRepository;
    private final StoreRepository storeRepository;
    private final SubscriptionService subscriptionService;

    public ListingServiceImpl(ListingRepository listingRepository,
                              StoreRepository storeRepository,
                              SubscriptionService subscriptionService) {
        this.listingRepository = listingRepository;
        this.storeRepository = storeRepository;
        this.subscriptionService = subscriptionService;
    }

    @Override
    @Transactional
    public Listing createListing(Long storeId, Listing listing, List<String> imagePaths) {
        subscriptionService.assertStoreCanCreateListing(storeId);

        var store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Store not found"));

        listing.setStore(store);
        listing.setPublishedAt(Instant.now());

        if (imagePaths != null) {
            int sort = 0;
            for (String path : imagePaths) {
                ListingImage img = new ListingImage();
                img.setImagePath(path);
                img.setSortOrder(sort++);
                img.setIsCover(sort == 1); // ilk resim cover
                listing.addImage(img);     // max10 burada kontrol
            }
        }

        return listingRepository.save(listing);
    }
}
