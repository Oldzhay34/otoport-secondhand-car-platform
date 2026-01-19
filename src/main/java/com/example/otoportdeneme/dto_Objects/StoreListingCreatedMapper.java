package com.example.otoportdeneme.dto_Objects;

import com.example.otoportdeneme.models.Listing;

public final class StoreListingCreatedMapper {

    private StoreListingCreatedMapper(){}

    public static StoreListingCreatedDto toDto(Listing l) {
        StoreListingCreatedDto dto = new StoreListingCreatedDto();
        dto.setId(l.getId());
        dto.setTitle(l.getTitle());
        dto.setPrice(l.getPrice());
        dto.setCurrency(l.getCurrency());
        dto.setCity(l.getCity());
        dto.setDistrict(l.getDistrict());

        // Lazy issue yaşamamak için: service içinde cover hesaplayıp set etmek daha iyi.
        // Ama Listing.getCoverImageUrl() LAZY patlatıyorsa burada çağırma.
        // Şimdilik null bırakabilirsin veya service tarafında hazırla.
        return dto;
    }
}
