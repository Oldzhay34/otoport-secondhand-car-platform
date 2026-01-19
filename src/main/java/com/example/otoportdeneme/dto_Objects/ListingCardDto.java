package com.example.otoportdeneme.dto_Objects;

import java.math.BigDecimal;

public class ListingCardDto {

    private Long id;

    private Long storeId;
    private String storeName;

    private String title;
    private BigDecimal price;
    private String currency;

    private String city;
    private String district;

    private Integer year;
    private Integer kilometer;

    private Long viewCount;
    private Long favoriteCount;

    // ✅ Filtre alanları
    private String brand;
    private String model;
    private String engine;       // trim adı
    private String bodyType;     // SEDAN/HATCHBACK...
    private String fuelType;
    private String transmission;

    private String coverImageUrl;

    public ListingCardDto() {}

    // -------- getters --------
    public Long getId() { return id; }

    public Long getStoreId() { return storeId; }
    public String getStoreName() { return storeName; }

    public String getTitle() { return title; }
    public BigDecimal getPrice() { return price; }
    public String getCurrency() { return currency; }

    public String getCity() { return city; }
    public String getDistrict() { return district; }

    public Integer getYear() { return year; }
    public Integer getKilometer() { return kilometer; }

    public Long getViewCount() { return viewCount; }
    public Long getFavoriteCount() { return favoriteCount; }

    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public String getEngine() { return engine; }
    public String getBodyType() { return bodyType; }
    public String getFuelType() { return fuelType; }
    public String getTransmission() { return transmission; }

    public String getCoverImageUrl() { return coverImageUrl; }

    // -------- setters --------
    public void setId(Long id) { this.id = id; }

    public void setStoreId(Long storeId) { this.storeId = storeId; }
    public void setStoreName(String storeName) { this.storeName = storeName; }

    public void setTitle(String title) { this.title = title; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setCurrency(String currency) { this.currency = currency; }

    public void setCity(String city) { this.city = city; }
    public void setDistrict(String district) { this.district = district; }

    public void setYear(Integer year) { this.year = year; }
    public void setKilometer(Integer kilometer) { this.kilometer = kilometer; }

    public void setViewCount(Long viewCount) { this.viewCount = viewCount; }
    public void setFavoriteCount(Long favoriteCount) { this.favoriteCount = favoriteCount; }

    public void setBrand(String brand) { this.brand = brand; }
    public void setModel(String model) { this.model = model; }
    public void setEngine(String engine) { this.engine = engine; }

    public void setBodyType(String bodyType) { this.bodyType = bodyType; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }
    public void setTransmission(String transmission) { this.transmission = transmission; }

    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }
}
