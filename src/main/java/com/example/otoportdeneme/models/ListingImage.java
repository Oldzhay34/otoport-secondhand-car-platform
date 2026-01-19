package com.example.otoportdeneme.models;

import jakarta.persistence.*;

@Entity
@Table(name = "listing_images", indexes = {
        @Index(name = "ix_listing_image_listing", columnList = "listing_id"),
        @Index(name = "ix_listing_image_cover", columnList = "listing_id,isCover")
})
public class ListingImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Hangi ilana ait?
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "listing_id", nullable = false)
    private Listing listing;

    // ✅ uploads klasöründeki dosya yolu (URL değil!)
    // örn: "uploads/listings/123/1.jpg"
    @Column(nullable = false, length = 255)
    private String imagePath;

    @Column(nullable = false)
    private Integer sortOrder = 0;

    @Column(nullable = false)
    private Boolean isCover = false;

    public ListingImage() {}

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Listing getListing() { return listing; }
    public void setListing(Listing listing) { this.listing = listing; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public Boolean getIsCover() { return isCover; }
    public void setIsCover(Boolean cover) { isCover = cover; }

    public String getUrl() {
        return imagePath;
    }
}
