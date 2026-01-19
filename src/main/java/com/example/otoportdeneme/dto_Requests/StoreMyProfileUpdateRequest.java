package com.example.otoportdeneme.dto_Requests;

public class StoreMyProfileUpdateRequest {

    private String storeName;
    private String authorizedPerson;
    private String taxNo;
    private String website;

    private String phone;

    private String city;
    private String district;
    private String addressLine;

    private Integer floor;
    private String shopNo;
    private String directionNote;

    public StoreMyProfileUpdateRequest() {}

    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }

    public String getAuthorizedPerson() { return authorizedPerson; }
    public void setAuthorizedPerson(String authorizedPerson) { this.authorizedPerson = authorizedPerson; }

    public String getTaxNo() { return taxNo; }
    public void setTaxNo(String taxNo) { this.taxNo = taxNo; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getAddressLine() { return addressLine; }
    public void setAddressLine(String addressLine) { this.addressLine = addressLine; }

    public Integer getFloor() { return floor; }
    public void setFloor(Integer floor) { this.floor = floor; }

    public String getShopNo() { return shopNo; }
    public void setShopNo(String shopNo) { this.shopNo = shopNo; }

    public String getDirectionNote() { return directionNote; }
    public void setDirectionNote(String directionNote) { this.directionNote = directionNote; }
}
