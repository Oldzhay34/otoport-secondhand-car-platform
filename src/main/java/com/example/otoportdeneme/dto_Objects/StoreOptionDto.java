package com.example.otoportdeneme.dto_Objects;

public class StoreOptionDto {
    private Long id;
    private String name;
    private String city;
    private String district;

    public StoreOptionDto() {}

    public StoreOptionDto(Long id, String name, String city, String district) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.district = district;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
}
