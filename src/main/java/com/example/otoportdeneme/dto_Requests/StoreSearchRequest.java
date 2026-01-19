package com.example.otoportdeneme.dto_Requests;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public class StoreSearchRequest {

    @Size(max = 120)
    private String q; // storeName / city / district araması

    @Min(1)
    @Max(8)
    private Integer floor;

    @Min(1)
    @Max(200)
    private Integer limit = 50;

    public StoreSearchRequest() {}

    public String getQ() { return q; }
    public void setQ(String q) { this.q = q; }

    public Integer getFloor() { return floor; }
    public void setFloor(Integer floor) { this.floor = floor; }

    public Integer getLimit() { return limit; }
    public void setLimit(Integer limit) { this.limit = limit; }
}
