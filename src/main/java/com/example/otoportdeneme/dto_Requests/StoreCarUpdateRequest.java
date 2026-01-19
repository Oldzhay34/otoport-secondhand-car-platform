package com.example.otoportdeneme.dto_Requests;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class StoreCarUpdateRequest {

    @Size(max = 150)
    private String title;

    private String description;

    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal price;

    @Size(min = 3, max = 3)
    private String currency;

    private Boolean negotiable;

    @Size(max = 60)
    private String city;

    @Size(max = 60)
    private String district;

    // ---- Car fields (opsiyonel güncelleme) ----
    @Min(2020) @Max(2023)
    private Integer year;

    @Min(0)
    private Integer kilometer;

    @Size(max = 60)
    private String color;

    // engine fields (opsiyonel)
    private Integer engineVolumeCc;
    private Integer enginePowerHp;

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

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public Integer getKilometer() { return kilometer; }
    public void setKilometer(Integer kilometer) { this.kilometer = kilometer; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public Integer getEngineVolumeCc() { return engineVolumeCc; }
    public void setEngineVolumeCc(Integer engineVolumeCc) { this.engineVolumeCc = engineVolumeCc; }

    public Integer getEnginePowerHp() { return enginePowerHp; }
    public void setEnginePowerHp(Integer enginePowerHp) { this.enginePowerHp = enginePowerHp; }
}
