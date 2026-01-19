package com.example.otoportdeneme.repositories;


import com.example.otoportdeneme.Enums.CampaignStatus;
import com.example.otoportdeneme.models.AdCampaign;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdCampaignRepository extends JpaRepository<AdCampaign, Long> {
    List<AdCampaign> findByStoreIdOrderByCreatedAtDesc(Long storeId);
    List<AdCampaign> findByStatusOrderByStartAtDesc(CampaignStatus status);
}
