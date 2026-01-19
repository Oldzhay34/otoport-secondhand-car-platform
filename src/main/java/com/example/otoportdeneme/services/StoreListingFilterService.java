package com.example.otoportdeneme.services;

import com.example.otoportdeneme.Enums.BodyType;
import com.example.otoportdeneme.Enums.FeatureMatchMode;
import com.example.otoportdeneme.models.Listing;

import java.util.List;

public interface StoreListingFilterService {

    List<Listing> filterStoreListings(
            Long storeId,                 // zorunlu
            BodyType bodyType,            // null => tümü
            List<Long> featureIds,        // empty/null => tümü
            FeatureMatchMode matchMode,   // null => ANY
            Integer floor,                // null => tümü (istersen kaldırırız)
            int limit
    );
}
