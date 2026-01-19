package com.example.otoportdeneme.dto_Requests;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class StoreListingCreateRequest {

    @NotBlank @Size(max = 150)
    private String title;

    private String description;

    @NotNull @DecimalMin(value="0.0", inclusive=false)
    private BigDecimal price;

    @NotBlank @Size(min=3, max=3)
    private String currency = "TRY";

    @NotNull
    private Boolean negotiable = true;

    @NotBlank @Size(max=60)
    private String city;

    @Size(max=60)
    private String district;

    // ✅ JSON'dan seçim
    @NotBlank @Size(max=80)
    private String brand;

    @NotBlank @Size(max=80)
    private String model;

    @Size(max=80)
    private String variant;    // opsiyonel

    @Size(max=80)
    private String engine;     // opsiyonel

    @Size(max=120)
    private String carPackage; // opsiyonel

    // ✅ Car zorunlular (senin Car entity)
    @NotNull
    private String transmission; // enum name: MANUAL/AUTOMATIC...

    @NotNull
    private String fuelType;     // enum name

    @NotNull
    private String bodyType;     // enum name

    @NotNull @Min(1990) @Max(2100)
    private Integer year;

    @NotNull @Min(0)
    private Integer kilometer;

    @Size(max=60)
    private String color;

    private Integer engineVolumeCc;
    private Integer enginePowerHp;

    // getters/setters...
    public String getTitle(){return title;}
    public void setTitle(String title){this.title=title;}
    public String getDescription(){return description;}
    public void setDescription(String description){this.description=description;}
    public BigDecimal getPrice(){return price;}
    public void setPrice(BigDecimal price){this.price=price;}
    public String getCurrency(){return currency;}
    public void setCurrency(String currency){this.currency=currency;}
    public Boolean getNegotiable(){return negotiable;}
    public void setNegotiable(Boolean negotiable){this.negotiable=negotiable;}
    public String getCity(){return city;}
    public void setCity(String city){this.city=city;}
    public String getDistrict(){return district;}
    public void setDistrict(String district){this.district=district;}
    public String getBrand(){return brand;}
    public void setBrand(String brand){this.brand=brand;}
    public String getModel(){return model;}
    public void setModel(String model){this.model=model;}
    public String getVariant(){return variant;}
    public void setVariant(String variant){this.variant=variant;}
    public String getEngine(){return engine;}
    public void setEngine(String engine){this.engine=engine;}
    public String getCarPackage(){return carPackage;}
    public void setCarPackage(String carPackage){this.carPackage=carPackage;}
    public String getTransmission(){return transmission;}
    public void setTransmission(String transmission){this.transmission=transmission;}
    public String getFuelType(){return fuelType;}
    public void setFuelType(String fuelType){this.fuelType=fuelType;}
    public String getBodyType(){return bodyType;}
    public void setBodyType(String bodyType){this.bodyType=bodyType;}
    public Integer getYear(){return year;}
    public void setYear(Integer year){this.year=year;}
    public Integer getKilometer(){return kilometer;}
    public void setKilometer(Integer kilometer){this.kilometer=kilometer;}
    public String getColor(){return color;}
    public void setColor(String color){this.color=color;}
    public Integer getEngineVolumeCc(){return engineVolumeCc;}
    public void setEngineVolumeCc(Integer engineVolumeCc){this.engineVolumeCc=engineVolumeCc;}
    public Integer getEnginePowerHp(){return enginePowerHp;}
    public void setEnginePowerHp(Integer enginePowerHp){this.enginePowerHp=enginePowerHp;}


}
