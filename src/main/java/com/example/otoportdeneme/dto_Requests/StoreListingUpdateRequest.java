package com.example.otoportdeneme.dto_Requests;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public class StoreListingUpdateRequest {

    @Size(max = 120)
    private String title;

    @Size(max = 5000)
    private String description;

    @DecimalMin("0.0")
    private BigDecimal price;

    private Boolean negotiable;

    @Size(max = 60)
    private String city;

    @Size(max = 60)
    private String district;

    // max 10 resim kontrolü service'te var
    private List<String> imagePaths;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Boolean getNegotiable() { return negotiable; }
    public void setNegotiable(Boolean negotiable) { this.negotiable = negotiable; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public List<String> getImagePaths() { return imagePaths; }
    public void setImagePaths(List<String> imagePaths) { this.imagePaths = imagePaths; }
}
