package com.example.otoportdeneme.dto_Requests;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class BuildingCreateRequest {

    @NotBlank
    @Size(max = 120)
    private String name;

    @Size(max = 60)
    private String city;

    @Size(max = 60)
    private String district;

    @Size(max = 255)
    private String addressLine;

    @Min(1)
    @Max(8)
    private Integer totalFloors = 8;

    public BuildingCreateRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getAddressLine() { return addressLine; }
    public void setAddressLine(String addressLine) { this.addressLine = addressLine; }

    public Integer getTotalFloors() { return totalFloors; }
    public void setTotalFloors(Integer totalFloors) { this.totalFloors = totalFloors; }
}
