package com.example.otoportdeneme.dto_Requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class StoreRegisterRequest {

    @NotBlank
    @Size(max = 140)
    private String storeName;

    @Email
    @NotBlank
    @Size(max = 190)
    private String email;

    @NotBlank
    @Size(min = 6, max = 72)
    private String password; // service içinde hashle

    @Size(max = 30)
    private String phone;

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
    private String shopNo;
    private String directionNote;

    public StoreRegisterRequest() {}

    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAuthorizedPerson() { return authorizedPerson; }
    public void setAuthorizedPerson(String authorizedPerson) { this.authorizedPerson = authorizedPerson; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

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
