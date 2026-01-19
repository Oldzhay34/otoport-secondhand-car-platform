package com.example.otoportdeneme.dto_Objects;

import java.math.BigDecimal;
import java.time.Instant;

public class FavoriteCardDto {
    private Long listingId;
    private String title;
    private BigDecimal price;
    private String currency;
    private String city;
    private Integer year;
    private Integer kilometer;
    private String coverImageUrl;
    private Instant favoritedAt;

    public FavoriteCardDto() {}

    public FavoriteCardDto(Long listingId, String title, BigDecimal price, String currency,
                           String city, Integer year, Integer kilometer, String coverImageUrl,
                           Instant favoritedAt) {
        this.listingId = listingId;
        this.title = title;
        this.price = price;
        this.currency = currency;
        this.city = city;
        this.year = year;
        this.kilometer = kilometer;
        this.coverImageUrl = coverImageUrl;
        this.favoritedAt = favoritedAt;
    }

    public Long getListingId() { return listingId; }
    public void setListingId(Long listingId) { this.listingId = listingId; }

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

    public Instant getFavoritedAt() { return favoritedAt; }
    public void setFavoritedAt(Instant favoritedAt) { this.favoritedAt = favoritedAt; }
}
