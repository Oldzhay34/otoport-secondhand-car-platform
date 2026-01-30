package com.example.otoportdeneme.services;

import com.example.otoportdeneme.Enums.SubscriptionPlan;
import com.example.otoportdeneme.Interfaces.StoreCardView;
import com.example.otoportdeneme.models.StoreSubscription;
import com.example.otoportdeneme.repositories.StoreSubscriptionRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Primary
@Service
public class StoreRankingStrategyImpl implements StoreRankingStrategy {

    private final StoreSubscriptionRepository subRepo;

    public StoreRankingStrategyImpl(StoreSubscriptionRepository subRepo) {
        this.subRepo = subRepo;
    }

    @Override
    public List<StoreCardView> rank(List<StoreCardView> input) {
        if (input == null || input.size() <= 1) return input;

        // storeId list
        List<Long> ids = input.stream()
                .map(StoreCardView::getId)
                .filter(Objects::nonNull)
                .toList();

        Map<Long, StoreSubscription> subs = subRepo.findAll().stream()
                .filter(s -> s.getStore() != null && s.getStore().getId() != null)
                .collect(Collectors.toMap(s -> s.getStore().getId(), s -> s, (a,b) -> a));

        // Weighted shuffle (Efraimidis-Spirakis)
        List<Ranked> tmp = new ArrayList<>(input.size());
        Random rnd = new Random();

        for (StoreCardView s : input) {
            int w = weightOf(subs.get(s.getId()));
            double u = Math.max(1e-12, rnd.nextDouble()); // avoid ln(0)
            double key = -Math.log(u) / (double) w;
            tmp.add(new Ranked(s, key));
        }

        tmp.sort(Comparator.comparingDouble(r -> r.key));
        return tmp.stream().map(r -> r.store).toList();
    }

    private int weightOf(StoreSubscription sub) {
        // subscription yoksa BASIC
        if (sub == null) return SubscriptionPlan.BASIC.weight;

        int limit = sub.getListingLimit() == null ? 10 : sub.getListingLimit();
        int featured = sub.getFeaturedLimit() == null ? 0 : sub.getFeaturedLimit();

        // Plan çıkar (limit/featured)
        if (limit >= 30 || featured >= 5) return SubscriptionPlan.PRO.weight;
        if (limit >= 20 || featured >= 2) return SubscriptionPlan.PLUS.weight;
        return SubscriptionPlan.BASIC.weight;
    }

    private static class Ranked {
        final StoreCardView store;
        final double key;
        Ranked(StoreCardView store, double key) { this.store = store; this.key = key; }
    }
}
