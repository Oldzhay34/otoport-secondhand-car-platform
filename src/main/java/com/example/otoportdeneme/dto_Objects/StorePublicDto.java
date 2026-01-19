package com.example.otoportdeneme.dto_Objects;

public class StorePublicDto {
    private Long id;
    private String storeName;
    private Boolean verified;

    private String city;
    private String district;
    private String addressLine;

    private String phone;
    private String website;

    private Integer floor;
    private String shopNo;
    private String directionNote;

    public StorePublicDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }

    public Boolean getVerified() { return verified; }
    public void setVerified(Boolean verified) { this.verified = verified; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getAddressLine() { return addressLine; }
    public void setAddressLine(String addressLine) { this.addressLine = addressLine; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public Integer getFloor() { return floor; }
    public void setFloor(Integer floor) { this.floor = floor; }

    public String getShopNo() { return shopNo; }
    public void setShopNo(String shopNo) { this.shopNo = shopNo; }

    public String getDirectionNote() { return directionNote; }
    public void setDirectionNote(String directionNote) { this.directionNote = directionNote; }
}
