package com.example.otoportdeneme.dto_Objects;

import com.example.otoportdeneme.Interfaces.StoreMyProfileView;

public final class StoreMyProfileMapper {

    private StoreMyProfileMapper() {}

    public static StoreMyProfileDto toDto(StoreMyProfileView v) {
        if (v == null) return null;

        StoreMyProfileDto dto = new StoreMyProfileDto();
        dto.setId(v.getId());
        dto.setStoreName(v.getStoreName());
        dto.setAuthorizedPerson(v.getAuthorizedPerson());
        dto.setWebsite(v.getWebsite());
        dto.setEmail(v.getEmail());
        dto.setPhone(v.getPhone());
        dto.setTaxNo(v.getTaxNo());
        dto.setCity(v.getCity());
        dto.setDistrict(v.getDistrict());
        dto.setAddressLine(v.getAddressLine());
        dto.setVerified(v.getVerified());
        dto.setListingLimit(v.getListingLimit());
        dto.setFloor(v.getFloor());
        dto.setShopNo(v.getShopNo());
        dto.setDirectionNote(v.getDirectionNote());
        dto.setLogoUrl(v.getLogoUrl());
        return dto;
    }
}
