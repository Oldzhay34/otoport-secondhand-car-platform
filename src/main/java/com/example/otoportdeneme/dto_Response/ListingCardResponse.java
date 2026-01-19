package com.example.otoportdeneme.dto_Response;

import java.math.BigDecimal;

public class ListingCardResponse {
    private Long id;
    private Long storeId;
    private String storeName;

    private String title;
    private BigDecimal price;
    private String currency;

    private String city;
    private Integer year;
    private Integer kilometer;

    private String coverImageUrl;

    public ListingCardResponse() {}

    public ListingCardResponse(Long id, Long storeId, String storeName,
                               String title, BigDecimal price, String currency,
                               String city, Integer year, Integer kilometer,
                               String coverImageUrl) {
        this.id = id;
        this.storeId = storeId;
        this.storeName = storeName;
        this.title = title;
        this.price = price;
        this.currency = currency;
        this.city = city;
        this.year = year;
        this.kilometer = kilometer;
        this.coverImageUrl = coverImageUrl;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }

    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public Integer getKilometer() { return kilometer; }
    public void setKilometer(Integer kilometer) { this.kilometer = kilometer; }

    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }
}
