package com.example.otoportdeneme.dto_Requests;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Max;

import java.math.BigDecimal;
import java.util.List;

public class ListingUpdateRequest {

    @Size(max = 150)
    private String title;

    private String description;

    @DecimalMin("0.0")
    private BigDecimal price;

    private Boolean negotiable;

    @Size(max = 60)
    private String city;

    @Size(max = 60)
    private String district;

    // uploads/... şeklinde path listesi
    @Max(10)
    private Integer imageCountGuard; // opsiyonel; sadece validation amaçlı (kullanmak zorunda değilsin)

    private List<String> imagePaths; // null => resim güncelleme yok

    public ListingUpdateRequest() {}

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

    public Integer getImageCountGuard() { return imageCountGuard; }
    public void setImageCountGuard(Integer imageCountGuard) { this.imageCountGuard = imageCountGuard; }

    public List<String> getImagePaths() { return imagePaths; }
    public void setImagePaths(List<String> imagePaths) { this.imagePaths = imagePaths; }
}
