package com.example.otoportdeneme.services;

import com.example.otoportdeneme.Interfaces.StoreCardView;
import com.example.otoportdeneme.repositories.StoreRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;   // ✅ DOĞRU
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;
    private final StoreRankingStrategy rankingStrategy;

    public StoreServiceImpl(StoreRepository storeRepository,
                            StoreRankingStrategy rankingStrategy) {
        this.storeRepository = storeRepository;
        this.rankingStrategy = rankingStrategy;
    }

    @Override
    public List<StoreCardView> getHomepageStores(int limit) {
        if (limit <= 0) limit = 50;

        Pageable pageable = PageRequest.of(0, limit);

        // ✅ cast yok, doğru pageable
        List<StoreCardView> stores =
                storeRepository.findAllByOrderByIdDesc(pageable);

        return rankingStrategy.rank(stores);
    }
}
