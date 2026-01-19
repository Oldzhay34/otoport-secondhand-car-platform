package com.example.otoportdeneme.dto_Objects;

import java.math.BigDecimal;
import java.time.Instant;

public class FavoriteItemDto {

    private Long listingId;
    private Long storeId;
    private String storeName;

    private String title;
    private Long price;
    private String currency;
    private String city;
    private String district;
    private Integer year;
    private Integer kilometer;
    private String coverImageUrl;

    private Instant createdAt;

    public FavoriteItemDto() {}

    public FavoriteItemDto(Long listingId, Long storeId, String storeName,
                           String title, Long price, String currency,
                           String city, String district,
                           Integer year, Integer kilometer,
                           String coverImageUrl,
                           Instant createdAt) {
        this.listingId = listingId;
        this.storeId = storeId;
        this.storeName = storeName;
        this.title = title;
        this.price = price;
        this.currency = currency;
        this.city = city;
        this.district = district;
        this.year = year;
        this.kilometer = kilometer;
        this.coverImageUrl = coverImageUrl;
        this.createdAt = createdAt;
    }

    public FavoriteItemDto(Long id, Long storeId, String storeName, String title, BigDecimal price, String currency, String city, String district, Integer year, Integer kilometer, String coverImageUrl, Instant createdAt) {
    }

    public Long getListingId() { return listingId; }
    public void setListingId(Long listingId) { this.listingId = listingId; }

    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }

    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Long getPrice() { return price; }
    public void setPrice(Long price) { this.price = price; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public Integer getKilometer() { return kilometer; }
    public void setKilometer(Integer kilometer) { this.kilometer = kilometer; }

    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
