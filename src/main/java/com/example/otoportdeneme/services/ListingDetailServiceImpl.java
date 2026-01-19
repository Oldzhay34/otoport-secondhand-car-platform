package com.example.otoportdeneme.services;

import com.example.otoportdeneme.Enums.ListingStatus;
import com.example.otoportdeneme.dto_Response.ListingCardResponse;
import com.example.otoportdeneme.dto_Objects.ListingDetailDto;
import com.example.otoportdeneme.dto_Objects.ListingDetailMapper;
import com.example.otoportdeneme.models.Listing;
import com.example.otoportdeneme.repositories.FavoriteRepository;
import com.example.otoportdeneme.repositories.ListingRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ListingDetailServiceImpl implements ListingDetailService {

    private final ListingRepository listingRepository;
    private final FavoriteRepository favoriteRepository;

    public ListingDetailServiceImpl(ListingRepository listingRepository,
                                    FavoriteRepository favoriteRepository) {
        this.listingRepository = listingRepository;
        this.favoriteRepository = favoriteRepository;
    }

    @Override
    @Transactional
    public ListingDetailDto getListingDetail(Long listingId, Long clientIdNullable) {

        // ✅ LAZY fix: fetch join ile çek
        Listing listing = listingRepository.findDetailById(listingId);
        if (listing == null) throw new IllegalArgumentException("Listing not found");

        ListingDetailDto dto = ListingDetailMapper.toDto(listing);

        if (clientIdNullable != null) {
            boolean fav = favoriteRepository.existsByClientIdAndListingId(clientIdNullable, listingId);
            dto.setFavoritedByMe(fav);
        } else {
            dto.setFavoritedByMe(null);
        }

        return dto;
    }

    @Override
    @Transactional
    public List<ListingCardResponse> filterListings(
            Long storeId,
            String brand,
            String model,
            String engine,
            String pack,
            Integer yearMin,
            Integer yearMax
    ) {
        // ✅ findAll() YOK. fetch join ile çekiyoruz.
        // İstersen status ACTIVE ver.
        List<Listing> all = listingRepository.findForFilter(storeId, ListingStatus.ACTIVE);

        return all.stream()
                .filter(l -> isBlank(brand) || equalsIgnoreCaseSafe(l.getBrand(), brand))
                .filter(l -> isBlank(model) || equalsIgnoreCaseSafe(l.getModel(), model))
                .filter(l -> isBlank(engine) || equalsIgnoreCaseSafe(l.getEngine(), engine))
                .filter(l -> isBlank(pack) || equalsIgnoreCaseSafe(l.getPack(), pack))

                .filter(l -> yearMin == null || (l.getYear() != null && l.getYear() >= yearMin))
                .filter(l -> yearMax == null || (l.getYear() != null && l.getYear() <= yearMax))

                .map(this::toCardResponse)
                .toList();
    }

    private ListingCardResponse toCardResponse(Listing l) {
        Long storeId = (l.getStore() != null) ? l.getStore().getId() : null;
        String storeName = (l.getStore() != null) ? l.getStore().getStoreName() : null;

        return new ListingCardResponse(
                l.getId(),
                storeId,
                storeName,
                l.getTitle(),
                l.getPrice(),
                l.getCurrency(),
                l.getCity(),
                l.getYear(),
                l.getKilometer(),
                l.getCoverImageUrl() // ✅ images fetch edildiği için artık patlamaz
        );
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private boolean equalsIgnoreCaseSafe(String a, String b) {
        if (a == null || b == null) return false;
        return a.trim().equalsIgnoreCase(b.trim());
    }
}
