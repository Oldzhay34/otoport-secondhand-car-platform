package com.example.otoportdeneme.Interfaces;

public interface StoreMyProfileView {
    Long getId();

    String getStoreName();
    String getAuthorizedPerson();
    String getTaxNo();
    String getWebsite();

    String getEmail();
    String getPhone();

    String getCity();
    String getDistrict();
    String getAddressLine();

    Boolean getVerified();
    Integer getListingLimit();

    Integer getFloor();
    String getShopNo();
    String getDirectionNote();
    String getLogoUrl();
}
