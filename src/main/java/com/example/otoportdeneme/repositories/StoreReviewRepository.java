package com.example.otoportdeneme.repositories;


import com.example.otoportdeneme.models.StoreReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreReviewRepository extends JpaRepository<StoreReview, Long> {
    List<StoreReview> findByStoreIdOrderByCreatedAtDesc(Long storeId);
    boolean existsByStoreIdAndClientId(Long storeId, Long clientId);
}
