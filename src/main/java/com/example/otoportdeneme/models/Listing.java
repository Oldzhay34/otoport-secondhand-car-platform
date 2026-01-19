package com.example.otoportdeneme.models;

import com.example.otoportdeneme.Enums.ListingStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "listings", indexes = {
        @Index(name = "ix_listing_store", columnList = "store_id"),
        @Index(name = "ix_listing_car", columnList = "car_id"),
        @Index(name = "ix_listing_status", columnList = "status"),
        @Index(name = "ix_listing_city", columnList = "city"),
        @Index(name = "ix_listing_price", columnList = "price"),
        @Index(name = "ix_listing_created", columnList = "createdAt")
})
public class Listing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // İlanı açan mağaza
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY) // cascade yok
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(nullable = false, length = 3)
    private String currency = "TRY";

    @Column(nullable = false)
    private Boolean negotiable = true;

    @Column(nullable = false, length = 60)
    private String city;

    @Column(length = 60)
    private String district;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ListingStatus status = ListingStatus.ACTIVE;

    @Column(nullable = false)
    private Long viewCount = 0L;

    @Column(nullable = false)
    private Long favoriteCount = 0L;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    private Instant publishedAt;
    private Instant updatedAt;

    @Size(max = 10)
    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    private List<ListingImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "listing", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Favorite> favorites = new ArrayList<>();

    public Listing() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Store getStore() { return store; }
    public void setStore(Store store) { this.store = store; }

    public Car getCar() { return car; }
    public void setCar(Car car) { this.car = car; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCurrency() { return currency; }

    public Boolean getNegotiable() { return negotiable; }
    public void setNegotiable(Boolean negotiable) { this.negotiable = negotiable; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public ListingStatus getStatus() { return status; }
    public void setStatus(ListingStatus status) { this.status = status; }

    public Long getViewCount() { return viewCount; }

    public Integer getFavoriteCount() { return Math.toIntExact(favoriteCount); }
    public void setFavoriteCount(Long favoriteCount) { this.favoriteCount = favoriteCount; }

    public Instant getCreatedAt() { return createdAt; }

    public Instant getPublishedAt() { return publishedAt; }
    public void setPublishedAt(Instant publishedAt) { this.publishedAt = publishedAt; }

    public List<ListingImage> getImages() { return images; }
    public void setImages(List<ListingImage> images) { this.images = images; }

    public List<Favorite> getFavorites() { return favorites; }
    public void setFavorites(List<Favorite> favorites) { this.favorites = favorites; }

    public void addImage(ListingImage image) {
        if (images.size() >= 10) throw new IllegalStateException("A listing can have at most 10 images.");
        image.setListing(this);
        images.add(image);
    }

    public void removeImage(ListingImage image) {
        images.remove(image);
        image.setListing(null);
    }

    /* =========================================================
       ✅ PROXY GETTERS (filtreleme ve kart ekranı için)
       ========================================================= */

    @Transient
    public String getBrand() {
        if (car == null) return null;
        Brand b = car.getBrand();
        return (b != null) ? b.getName() : null; // Brand'da name alanı farklıysa düzelt
    }

    @Transient
    public String getModel() {
        if (car == null) return null;
        CarModel m = car.getModel();
        return (m != null) ? m.getName() : null; // CarModel'da name alanı farklıysa düzelt
    }

    /**
     * Senin sistemde "engine" string alanı yok.
     * Biz engine = car.getEngine() (yani trimName) olarak kabul ettik.
     */
    @Transient
    public String getEngine() {
        return (car != null) ? car.getEngine() : null;
    }

    /**
     * pack/variant alanları sende ayrı değil.
     * Eğer ileride Trim içinde "packageName" gibi alan eklenirse buraya bağlarız.
     */
    @Transient
    public String getPack() {
        return null;
    }

    @Transient
    public Integer getYear() {
        return (car != null) ? car.getYear() : null;
    }

    @Transient
    public Integer getKilometer() {
        return (car != null) ? car.getKilometer() : null;
    }

    /**
     * Cover image: listing images içinden ilkini döndür.
     * ListingImage'da "url" alanı farklıysa (imageUrl vs) düzelt.
     */
    @Transient
    public String getCoverImageUrl() {
        if (images == null || images.isEmpty()) return null;

        return images.stream()
                .filter(ListingImage::getIsCover)
                .findFirst()
                .map(ListingImage::getImagePath)
                .orElse(images.get(0).getImagePath());
    }

    public void setCurrency(String upperCase) {
        this.currency = upperCase;
    }
}
