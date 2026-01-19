package com.example.otoportdeneme.services;

public interface FavoriteService {
    void addFavorite(Long clientId, Long listingId);
    void removeFavorite(Long clientId, Long listingId);
}
