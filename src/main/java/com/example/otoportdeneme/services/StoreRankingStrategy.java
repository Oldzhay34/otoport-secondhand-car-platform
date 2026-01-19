package com.example.otoportdeneme.services;

import com.example.otoportdeneme.Interfaces.StoreCardView;

import java.util.List;

public interface StoreRankingStrategy {
    List<StoreCardView> rank(List<StoreCardView> input);
}
