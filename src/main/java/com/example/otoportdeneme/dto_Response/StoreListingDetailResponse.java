package com.example.otoportdeneme.dto_Response;

import com.example.otoportdeneme.dto_Objects.CarSummaryDto;
import com.example.otoportdeneme.dto_Objects.ListingImageDto;
import com.example.otoportdeneme.dto_Objects.StoreSummaryDto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class StoreListingDetailResponse {

    private Long id;

    private String title;
    private String description;

    private BigDecimal price;
    private String currency;
    private Boolean negotiable;

    private String city;
    private String district;

    private String status;

    private Long viewCount;
    private Long favoriteCount;

    private Instant createdAt;
    private Instant publishedAt;

    private StoreSummaryDto store;
    private CarSummaryDto car;
    private List<ListingImageDto> images;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public Boolean getNegotiable() { return negotiable; }
    public void setNegotiable(Boolean negotiable) { this.negotiable = negotiable; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getViewCount() { return viewCount; }
    public void setViewCount(Long viewCount) { this.viewCount = viewCount; }

    public Long getFavoriteCount() { return favoriteCount; }
    public void setFavoriteCount(Long favoriteCount) { this.favoriteCount = favoriteCount; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getPublishedAt() { return publishedAt; }
    public void setPublishedAt(Instant publishedAt) { this.publishedAt = publishedAt; }

    public StoreSummaryDto getStore() { return store; }
    public void setStore(StoreSummaryDto store) { this.store = store; }

    public CarSummaryDto getCar() { return car; }
    public void setCar(CarSummaryDto car) { this.car = car; }

    public List<ListingImageDto> getImages() { return images; }
    public void setImages(List<ListingImageDto> images) { this.images = images; }
}
