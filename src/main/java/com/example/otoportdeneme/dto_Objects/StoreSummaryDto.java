package com.example.otoportdeneme.dto_Objects;

public class StoreSummaryDto {
    private Long id;
    private String storeName;
    private String city;
    private String district;
    private String phone;
    private String logoUrl;

    public StoreSummaryDto() {}

    public StoreSummaryDto(Long id, String storeName, String city, String district, String phone, String logoUrl) {
        this.id = id;
        this.storeName = storeName;
        this.city = city;
        this.district = district;
        this.phone = phone;
        this.logoUrl = logoUrl;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
}
