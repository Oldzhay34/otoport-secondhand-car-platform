package com.example.otoportdeneme.dto_Objects;

import java.util.ArrayList;
import java.util.List;

public class StoreHomeDto {
    private Long storeId;
    private String storeName;
    private String city;
    private String district;
    private Boolean verified;

    private List<StoreListingRowDto> listings = new ArrayList<>();

    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }

    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public Boolean getVerified() { return verified; }
    public void setVerified(Boolean verified) { this.verified = verified; }

    public List<StoreListingRowDto> getListings() { return listings; }
    public void setListings(List<StoreListingRowDto> listings) { this.listings = listings; }
}
