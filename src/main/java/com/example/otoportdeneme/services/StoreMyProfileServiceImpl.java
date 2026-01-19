package com.example.otoportdeneme.services;

import com.example.otoportdeneme.Interfaces.StoreMyProfileView;
import com.example.otoportdeneme.dto_Objects.StoreMyProfileDto;
import com.example.otoportdeneme.dto_Objects.StoreMyProfileMapper;
import com.example.otoportdeneme.dto_Requests.StoreMyProfileUpdateRequest;
import com.example.otoportdeneme.models.Store;
import com.example.otoportdeneme.repositories.StoreRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class StoreMyProfileServiceImpl implements StoreMyProfileService {

    private final StoreRepository storeRepository;

    public StoreMyProfileServiceImpl(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    @Override
    public StoreMyProfileDto getMyProfile(Long storeId) {
        StoreMyProfileView v = storeRepository.findMyProjectedById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Store not found"));

        return StoreMyProfileMapper.toDto(v);
    }

    @Override
    @Transactional
    public StoreMyProfileDto updateMyProfile(Long storeId, StoreMyProfileUpdateRequest req) {
        Store s = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Store not found"));

        if (req.getStoreName() == null || req.getStoreName().trim().isEmpty()) {
            throw new IllegalArgumentException("storeName is required");
        }

        s.setStoreName(req.getStoreName().trim());
        s.setAuthorizedPerson(trimOrNull(req.getAuthorizedPerson()));
        s.setTaxNo(trimOrNull(req.getTaxNo()));
        s.setWebsite(trimOrNull(req.getWebsite()));
        s.setPhone(trimOrNull(req.getPhone()));
        s.setCity(trimOrNull(req.getCity()));
        s.setDistrict(trimOrNull(req.getDistrict()));
        s.setAddressLine(trimOrNull(req.getAddressLine()));
        s.setFloor(req.getFloor());
        s.setShopNo(trimOrNull(req.getShopNo()));
        s.setDirectionNote(trimOrNull(req.getDirectionNote()));

        storeRepository.save(s);

        return getMyProfile(storeId);
    }

    private String trimOrNull(String s){
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
