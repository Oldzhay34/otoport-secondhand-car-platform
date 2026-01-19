package com.example.otoportdeneme.services;

import com.example.otoportdeneme.Interfaces.StoreCardView;
import org.springframework.stereotype.Component;
import com.example.otoportdeneme.services.StoreRankingStrategy;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class RandomStoreRankingStrategy implements StoreRankingStrategy {

    private final SecureRandom rnd = new SecureRandom();

    @Override
    public List<StoreCardView> rank(List<StoreCardView> input) {
        List<StoreCardView> copy = new ArrayList<>(input);
        Collections.shuffle(copy, rnd); // reklam bypass rastgele sıralama için bu
        return copy;
    }
}
