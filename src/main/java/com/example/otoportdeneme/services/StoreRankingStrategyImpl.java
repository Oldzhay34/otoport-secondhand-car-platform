package com.example.otoportdeneme.services;

import com.example.otoportdeneme.Interfaces.StoreCardView;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.context.annotation.Primary;


@Primary
@Service
public class StoreRankingStrategyImpl implements StoreRankingStrategy {
    @Override
    public List<StoreCardView> rank(List<StoreCardView> input) {
        return input;
    }
}
