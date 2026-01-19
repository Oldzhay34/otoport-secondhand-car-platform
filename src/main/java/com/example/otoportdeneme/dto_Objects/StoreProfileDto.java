package com.example.otoportdeneme.dto_Objects;

public class StoreProfileDto {
    private Long id;
    private String storeName;
    private String authorizedPerson;
    private String website;

    private String email;
    private String phone;

    private String city;
    private String district;
    private String addressLine;

    private Boolean verified;

    private Integer floor;      // 1..8
    private String shopNo;
    private String directionNote;

    public StoreProfileDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }

    public String getAuthorizedPerson() { return authorizedPerson; }
    public void setAuthorizedPerson(String authorizedPerson) { this.authorizedPerson = authorizedPerson; }

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

    public Integer getFloor() { return floor; }
    public void setFloor(Integer floor) { this.floor = floor; }

    public String getShopNo() { return shopNo; }
    public void setShopNo(String shopNo) { this.shopNo = shopNo; }

    public String getDirectionNote() { return directionNote; }
    public void setDirectionNote(String directionNote) { this.directionNote = directionNote; }
}
