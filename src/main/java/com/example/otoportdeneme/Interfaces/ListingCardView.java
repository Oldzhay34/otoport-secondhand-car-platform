package com.example.otoportdeneme.Interfaces;

import java.math.BigDecimal;

public interface ListingCardView {
    Long getId();
    String getTitle();
    BigDecimal getPrice();
    String getCurrency();
    String getCity();
    String getDistrict();
    Long getViewCount();
    Long getFavoriteCount();
}
