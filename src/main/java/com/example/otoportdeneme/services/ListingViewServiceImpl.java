package com.example.otoportdeneme.services;

import com.example.otoportdeneme.models.ListingView;
import com.example.otoportdeneme.repositories.ListingRepository;
import com.example.otoportdeneme.repositories.ListingViewRepository;
import com.example.otoportdeneme.repositories.UserAccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class ListingViewServiceImpl implements ListingViewService {

    private final ListingViewRepository listingViewRepository;
    private final ListingRepository listingRepository;
    private final UserAccountRepository userAccountRepository;

    public ListingViewServiceImpl(ListingViewRepository listingViewRepository,
                                  ListingRepository listingRepository,
                                  UserAccountRepository userAccountRepository) {
        this.listingViewRepository = listingViewRepository;
        this.listingRepository = listingRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    @Transactional
    public void registerViewOnce(Long userId, Long listingId) {

        if (listingViewRepository.existsByIdUserIdAndIdListingId(userId, listingId)) {
            return; // daha önce view yazılmış
        }

        var user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        var listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("Listing not found"));

        listingViewRepository.save(new ListingView(user, listing));
        listingRepository.incrementViewCount(listingId);
    }
}
