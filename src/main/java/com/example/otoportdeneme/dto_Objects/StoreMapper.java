package com.example.otoportdeneme.dto_Objects;

import com.example.otoportdeneme.Interfaces.StoreCardView;

public final class StoreMapper {

    private StoreMapper() {}

    public static StoreCardDto toDto(StoreCardView v) {
        if (v == null) return null;

        return new StoreCardDto(
                v.getId(),
                v.getStoreName(),
                v.getCity(),
                v.getDistrict(),
                v.getVerified(),
                v.getFloor(),
                v.getShopNo(),
                v.getDirectionNote(),
                v.getLogoUrl()
        );

    }
}
