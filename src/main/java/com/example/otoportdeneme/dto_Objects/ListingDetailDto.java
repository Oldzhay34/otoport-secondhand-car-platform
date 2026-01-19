package com.example.otoportdeneme.dto_Objects;

import com.example.otoportdeneme.Enums.ListingStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class ListingDetailDto {

    // ===== Listing basic =====
    private Long id;
    private String title;
    private String description;

    private BigDecimal price;
    private String currency;
    private Boolean negotiable;

    private String city;
    private String district;

    private ListingStatus status;
    private Long viewCount;
    private Integer favoriteCount;

    private Instant createdAt;
    private Instant publishedAt;

    // ===== Nested objects =====
    private StoreCardDto store;           // ListingDetailMapper setStore(...)
    private CarDetailDto car;             // ListingDetailMapper setCar(...)
    private List<String> features;        // ListingDetailMapper setFeatures(...)
    private List<ListingImageDto> images; // ListingDetailMapper setImages(...)
    private ExpertReportDto expertReport; // ListingDetailMapper setExpertReport(...)

    // ===== client-specific =====
    private Boolean favoritedByMe;        // ListingDetailServiceImpl setFavoritedByMe(...)

    // ---------------- GETTERS ----------------

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }

    public BigDecimal getPrice() { return price; }
    public String getCurrency() { return currency; }
    public Boolean getNegotiable() { return negotiable; }

    public String getCity() { return city; }
    public String getDistrict() { return district; }

    public ListingStatus getStatus() { return status; }
    public Long getViewCount() { return viewCount; }
    public Integer getFavoriteCount() { return favoriteCount; }

    public Instant getCreatedAt() { return createdAt; }
    public Instant getPublishedAt() { return publishedAt; }

    public StoreCardDto getStore() { return store; }
    public CarDetailDto getCar() { return car; }
    public List<String> getFeatures() { return features; }
    public List<ListingImageDto> getImages() { return images; }
    public ExpertReportDto getExpertReport() { return expertReport; }

    public Boolean getFavoritedByMe() { return favoritedByMe; }

    // ---------------- SETTERS ----------------

    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }

    public void setPrice(BigDecimal price) { this.price = price; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setNegotiable(Boolean negotiable) { this.negotiable = negotiable; }

    public void setCity(String city) { this.city = city; }
    public void setDistrict(String district) { this.district = district; }

    public void setStatus(ListingStatus status) { this.status = status; }
    public void setViewCount(Long viewCount) { this.viewCount = viewCount; }
    public void setFavoriteCount(Integer favoriteCount) { this.favoriteCount = favoriteCount; }

    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public void setPublishedAt(Instant publishedAt) { this.publishedAt = publishedAt; }

    public void setStore(StoreCardDto store) { this.store = store; }
    public void setCar(CarDetailDto car) { this.car = car; }
    public void setFeatures(List<String> features) { this.features = features; }
    public void setImages(List<ListingImageDto> images) { this.images = images; }
    public void setExpertReport(ExpertReportDto expertReport) { this.expertReport = expertReport; }

    public void setFavoritedByMe(Boolean favoritedByMe) { this.favoritedByMe = favoritedByMe; }


}
