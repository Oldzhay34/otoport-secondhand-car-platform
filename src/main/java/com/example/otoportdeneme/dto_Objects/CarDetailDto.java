package com.example.otoportdeneme.dto_Objects;

import com.example.otoportdeneme.Enums.BodyType;
import com.example.otoportdeneme.Enums.FuelType;
import com.example.otoportdeneme.Enums.Transmission;

public class CarDetailDto {

    private Long id;

    private String brandName;
    private String modelName;
    private String trimName;

    private Integer year;
    private Integer kilometer;

    private Transmission transmission;
    private FuelType fuelType;
    private BodyType bodyType;

    private Integer engineVolumeCc;
    private Integer enginePowerHp;
    private String color;

    public CarDetailDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }

    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }

    public String getTrimName() { return trimName; }
    public void setTrimName(String trimName) { this.trimName = trimName; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public Integer getKilometer() { return kilometer; }
    public void setKilometer(Integer kilometer) { this.kilometer = kilometer; }

    public Transmission getTransmission() { return transmission; }
    public void setTransmission(Transmission transmission) { this.transmission = transmission; }

    public FuelType getFuelType() { return fuelType; }
    public void setFuelType(FuelType fuelType) { this.fuelType = fuelType; }

    public BodyType getBodyType() { return bodyType; }
    public void setBodyType(BodyType bodyType) { this.bodyType = bodyType; }

    public Integer getEngineVolumeCc() { return engineVolumeCc; }
    public void setEngineVolumeCc(Integer engineVolumeCc) { this.engineVolumeCc = engineVolumeCc; }

    public Integer getEnginePowerHp() { return enginePowerHp; }
    public void setEnginePowerHp(Integer enginePowerHp) { this.enginePowerHp = enginePowerHp; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
}
