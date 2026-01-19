package com.example.otoportdeneme.dto_Objects;

public class StoreMyProfileDto {

    private Long id;

    private String storeName;
    private String authorizedPerson;
    private String taxNo;
    private String website;

    private String email;
    private String phone;

    private String city;
    private String district;
    private String addressLine;

    private Boolean verified;
    private Integer listingLimit;

    private Integer floor;
    private String shopNo;
    private String directionNote;

    private String logoUrl;

    public StoreMyProfileDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }

    public String getAuthorizedPerson() { return authorizedPerson; }
    public void setAuthorizedPerson(String authorizedPerson) { this.authorizedPerson = authorizedPerson; }

    public String getTaxNo() { return taxNo; }
    public void setTaxNo(String taxNo) { this.taxNo = taxNo; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getAddressLine() { return addressLine; }
    public void setAddressLine(String addressLine) { this.addressLine = addressLine; }

    public Boolean getVerified() { return verified; }
    public void setVerified(Boolean verified) { this.verified = verified; }

    public Integer getListingLimit() { return listingLimit; }
    public void setListingLimit(Integer listingLimit) { this.listingLimit = listingLimit; }

    public Integer getFloor() { return floor; }
    public void setFloor(Integer floor) { this.floor = floor; }

    public String getShopNo() { return shopNo; }
    public void setShopNo(String shopNo) { this.shopNo = shopNo; }

    public String getDirectionNote() { return directionNote; }
    public void setDirectionNote(String directionNote) { this.directionNote = directionNote; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
}
