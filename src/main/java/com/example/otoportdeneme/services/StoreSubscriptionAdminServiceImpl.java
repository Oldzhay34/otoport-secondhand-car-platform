package com.example.otoportdeneme.services;

import com.example.otoportdeneme.Enums.SubscriptionPlan;
import com.example.otoportdeneme.dto_Requests.StoreSubscriptionAdminRowDto;
import com.example.otoportdeneme.models.Store;
import com.example.otoportdeneme.models.StoreSubscription;
import com.example.otoportdeneme.repositories.StoreRepository;
import com.example.otoportdeneme.repositories.StoreSubscriptionRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Service
public class StoreSubscriptionAdminServiceImpl implements StoreSubscriptionAdminService {

    private final StoreRepository storeRepo;
    private final StoreSubscriptionRepository subRepo;

    public StoreSubscriptionAdminServiceImpl(StoreRepository storeRepo, StoreSubscriptionRepository subRepo) {
        this.storeRepo = storeRepo;
        this.subRepo = subRepo;
    }

    @Override
    @Transactional
    public List<StoreSubscriptionAdminRowDto> list() {
        return storeRepo.findAll().stream().map(store -> {
            StoreSubscription sub = subRepo.findByStoreId(store.getId()).orElse(null);
            SubscriptionPlan plan = toPlan(sub);

            StoreSubscriptionAdminRowDto dto = new StoreSubscriptionAdminRowDto();
            dto.storeId = store.getId();
            dto.storeName = store.getStoreName();
            dto.city = store.getCity();
            dto.district = store.getDistrict();
            dto.plan = plan.name();
            dto.listingLimit = plan.listingLimit;
            dto.featuredLimit = plan.featuredLimit;
            dto.isActive = sub == null ? true : sub.getIsActive();
            return dto;
        }).toList();
    }

    @Override
    @Transactional
    public void setPlan(Long storeId, String planStr) {
        SubscriptionPlan plan;
        try {
            plan = SubscriptionPlan.valueOf(planStr.trim().toUpperCase());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid plan");
        }

        Store store = storeRepo.findById(storeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Store not found"));

        StoreSubscription sub = subRepo.findByStoreId(storeId).orElseGet(() -> {
            StoreSubscription ns = new StoreSubscription();
            ns.setStore(store);
            ns.setIsActive(true);
            ns.setStartsAt(Instant.now());
            return ns;
        });

        sub.setListingLimit(plan.listingLimit);
        sub.setFeaturedLimit(plan.featuredLimit);
        sub.setIsActive(true);
        sub.setStartsAt(Instant.now());
        subRepo.save(sub);
    }

    private SubscriptionPlan toPlan(StoreSubscription sub) {
        if (sub == null) return SubscriptionPlan.BASIC;
        int limit = sub.getListingLimit() == null ? 10 : sub.getListingLimit();
        int feat = sub.getFeaturedLimit() == null ? 0 : sub.getFeaturedLimit();

        if (limit >= 30 || feat >= 5) return SubscriptionPlan.PRO;
        if (limit >= 20 || feat >= 2) return SubscriptionPlan.PLUS;
        return SubscriptionPlan.BASIC;
    }
}
