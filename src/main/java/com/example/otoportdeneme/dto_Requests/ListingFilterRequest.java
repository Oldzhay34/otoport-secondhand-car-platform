package com.example.otoportdeneme.dto_Requests;

import com.example.otoportdeneme.Enums.BodyType;
import com.example.otoportdeneme.Enums.FeatureMatchMode;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;

public class ListingFilterRequest {

    private BodyType bodyType;                 // null => tümü
    private List<Long> featureIds;             // null/empty => tümü
    private FeatureMatchMode matchMode;        // null => ANY

    @Min(1)
    @Max(8)
    private Integer floor;                     // null => tüm katlar

    @Min(1)
    @Max(200)
    private Integer limit = 50;

    public ListingFilterRequest() {}

    public BodyType getBodyType() { return bodyType; }
    public void setBodyType(BodyType bodyType) { this.bodyType = bodyType; }

    public List<Long> getFeatureIds() { return featureIds; }
    public void setFeatureIds(List<Long> featureIds) { this.featureIds = featureIds; }

    public FeatureMatchMode getMatchMode() { return matchMode; }
    public void setMatchMode(FeatureMatchMode matchMode) { this.matchMode = matchMode; }

    public Integer getFloor() { return floor; }
    public void setFloor(Integer floor) { this.floor = floor; }

    public Integer getLimit() { return limit; }
    public void setLimit(Integer limit) { this.limit = limit; }
}
