package com.example.otoportdeneme.services;

import com.example.otoportdeneme.Interfaces.StoreProfileView;
import com.example.otoportdeneme.repositories.StoreRepository;
import jakarta.transaction.Transactional;
import com.example.otoportdeneme.models.Store;
import org.springframework.stereotype.Service;

@Service
public class StoreProfileServiceImpl implements StoreProfileService {

    private final StoreRepository storeRepository;

    public StoreProfileServiceImpl(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    @Override
    public StoreProfileView getStoreProfileOrThrow(Long storeId) {
        return storeRepository.findProjectedById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Store not found"));
    }

    @Override
    @Transactional
    public StoreProfileView updateStoreProfile(
            Long storeId,
            String storeName,
            String authorizedPerson,
            String taxNo,
            String website,
            String city,
            String district,
            String addressLine,
            Integer floor,
            String shopNo,
            String directionNote,
            String phone
    ) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Store not found"));

        if (storeName != null && !storeName.isBlank()) store.setStoreName(storeName);
        if (authorizedPerson != null) store.setAuthorizedPerson(authorizedPerson);
        if (taxNo != null) store.setTaxNo(taxNo);
        if (website != null) store.setWebsite(website);

        if (city != null) store.setCity(city);
        if (district != null) store.setDistrict(district);
        if (addressLine != null) store.setAddressLine(addressLine);

        if (floor != null) {
            if (floor < 1 || floor > 8) throw new IllegalArgumentException("floor must be between 1 and 8");
            store.setFloor(floor);
        }

        if (shopNo != null) store.setShopNo(shopNo);
        if (directionNote != null) store.setDirectionNote(directionNote);

        // phone UserAccount alanı
        if (phone != null) store.setPhone(phone);

        storeRepository.save(store);
        return getStoreProfileOrThrow(storeId);
    }
}
