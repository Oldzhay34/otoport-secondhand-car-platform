package com.example.otoportdeneme.dto_Objects;

import java.math.BigDecimal;

public class StoreListingEditDto {

    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private String currency;
    private Boolean negotiable;
    private String city;
    private String district;

    // car
    private Long carId;
    private Integer year;
    private Integer kilometer;
    private String color;
    private Integer engineVolumeCc;
    private Integer enginePowerHp;

    private ExpertReportDto expertReport;

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

    public Long getCarId() { return carId; }
    public void setCarId(Long carId) { this.carId = carId; }

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

    public ExpertReportDto getExpertReport() { return expertReport; }
    public void setExpertReport(ExpertReportDto expertReport) { this.expertReport = expertReport; }
}
