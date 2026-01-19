package com.example.otoportdeneme.dto_Objects;

import com.example.otoportdeneme.Interfaces.StoreProfileView;

public final class StoreProfileMapper {

    private StoreProfileMapper() {}

    public static StoreProfileDto toDto(StoreProfileView v) {
        if (v == null) return null;

        StoreProfileDto dto = new StoreProfileDto();
        dto.setId(v.getId());
        dto.setStoreName(v.getStoreName());
        dto.setAuthorizedPerson(v.getAuthorizedPerson());
        dto.setWebsite(v.getWebsite());
        dto.setEmail(v.getEmail());
        dto.setPhone(v.getPhone());
        dto.setCity(v.getCity());
        dto.setDistrict(v.getDistrict());
        dto.setAddressLine(v.getAddressLine());
        dto.setVerified(v.getVerified());
        dto.setFloor(v.getFloor());
        dto.setShopNo(v.getShopNo());
        dto.setDirectionNote(v.getDirectionNote());
        return dto;
    }
}
