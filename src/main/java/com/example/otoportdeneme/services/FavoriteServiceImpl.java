package com.example.otoportdeneme.services;

import com.example.otoportdeneme.repositories.FavoriteRepository;
import com.example.otoportdeneme.repositories.ClientRepository;
import com.example.otoportdeneme.repositories.ListingRepository;
import jakarta.transaction.Transactional;
import com.example.otoportdeneme.models.Favorite;
import org.springframework.stereotype.Service;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final ClientRepository clientRepository;
    private final ListingRepository listingRepository;

    public FavoriteServiceImpl(FavoriteRepository favoriteRepository,
                               ClientRepository clientRepository,
                               ListingRepository listingRepository) {
        this.favoriteRepository = favoriteRepository;
        this.clientRepository = clientRepository;
        this.listingRepository = listingRepository;
    }

    @Override
    @Transactional
    public void addFavorite(Long clientId, Long listingId) {
        if (favoriteRepository.existsByClientIdAndListingId(clientId, listingId)) {
            return; // idempotent
        }

        var client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found"));
        var listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("Listing not found"));

        favoriteRepository.save(new Favorite(client, listing));
        listingRepository.incrementFavoriteCount(listingId);
    }

    @Override
    @Transactional
    public void removeFavorite(Long clientId, Long listingId) {
        if (!favoriteRepository.existsByClientIdAndListingId(clientId, listingId)) {
            return; // idempotent
        }

        favoriteRepository.deleteByClientIdAndListingId(clientId, listingId);
        listingRepository.decrementFavoriteCount(listingId);
    }
}
