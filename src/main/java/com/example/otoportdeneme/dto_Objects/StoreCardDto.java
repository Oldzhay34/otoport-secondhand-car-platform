package com.example.otoportdeneme.dto_Objects;

public class StoreCardDto {
    private Long id;
    private String storeName;
    private String city;
    private String district;
    private Boolean verified;
    private Integer floor;
    private String shopNo;
    private String directionNote;
    private String logoUrl;

    public StoreCardDto() {}

    public StoreCardDto(Long id, String storeName, String city, String district,
                        Boolean verified, Integer floor, String shopNo, String directionNote,String logoUrl) {
        this.id = id;
        this.storeName = storeName;
        this.city = city;
        this.district = district;
        this.verified = verified;
        this.floor = floor;
        this.shopNo = shopNo;
        this.directionNote = directionNote;
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

    public Boolean getVerified() { return verified; }
    public void setVerified(Boolean verified) { this.verified = verified; }

    public Integer getFloor() { return floor; }
    public void setFloor(Integer floor) { this.floor = floor; }

    public String getShopNo() { return shopNo; }
    public void setShopNo(String shopNo) { this.shopNo = shopNo; }

    public String getDirectionNote() { return directionNote; }
    public void setDirectionNote(String directionNote) { this.directionNote = directionNote; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
}
