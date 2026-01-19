package com.example.otoportdeneme.dto_Requests;

import jakarta.validation.constraints.Size;

public class StoreProfileUpdateRequest {

    @Size(max = 140)
    private String storeName;

    @Size(max = 80)
    private String authorizedPerson;

    @Size(max = 120)
    private String website;

    @Size(max = 60)
    private String city;

    @Size(max = 60)
    private String district;

    @Size(max = 255)
    private String addressLine;

    private Integer floor; // 1..8

    @Size(max = 20)
    private String shopNo;

    @Size(max = 255)
    private String directionNote;

    @Size(max = 30)
    private String phone;


    public String getStoreName() {
        return storeName;
    }

    public String getAuthorizedPerson() {
        return authorizedPerson;
    }

    public String getWebsite() {
        return website;
    }

    public String getCity() {
        return city;
    }

    public String getDistrict() {
        return district;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public Integer getFloor() {
        return floor;
    }

    public String getShopNo() {
        return shopNo;
    }

    public String getDirectionNote() {
        return directionNote;
    }

    public String getPhone() {
        return phone;
    }
}
