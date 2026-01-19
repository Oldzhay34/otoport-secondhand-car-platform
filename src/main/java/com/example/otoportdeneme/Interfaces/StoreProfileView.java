package com.example.otoportdeneme.Interfaces;

public interface StoreProfileView {
    Long getId();
    String getStoreName();
    String getAuthorizedPerson();
    String getTaxNo();
    String getWebsite();
    String getCity();
    String getDistrict();
    String getAddressLine();
    Boolean getVerified();
    Integer getListingLimit();
    Integer getFloor();
    String getShopNo();
    String getDirectionNote();
    String getEmail();
    String getPhone();

    String getLogoUrl();
}
