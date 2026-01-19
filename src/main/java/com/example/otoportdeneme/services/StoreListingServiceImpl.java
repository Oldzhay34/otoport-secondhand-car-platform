package com.example.otoportdeneme.services;

import com.example.otoportdeneme.dto_Objects.StoreListingEditDto;
import com.example.otoportdeneme.dto_Requests.StoreCarUpdateRequest;
import com.example.otoportdeneme.models.Car;
import com.example.otoportdeneme.models.Listing;
import com.example.otoportdeneme.repositories.ListingRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class StoreListingServiceImpl implements StoreListingService {

    private final ListingRepository listingRepository;

    public StoreListingServiceImpl(ListingRepository listingRepository) {
        this.listingRepository = listingRepository;
    }

    @Override
    @Transactional
    public StoreListingEditDto getMyListingForEdit(Long storeId, Long listingId) {
        Listing l = listingRepository.findById(listingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing not found"));

        requireOwner(l, storeId);
        return toEditDto(l);
    }

    @Override
    @Transactional
    public StoreListingEditDto updateMyListing(Long storeId, Long listingId, StoreCarUpdateRequest req) {
        Listing l = listingRepository.findById(listingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing not found"));

        requireOwner(l, storeId);

        // ---- Listing fields ----
        if (req.getTitle() != null && !req.getTitle().isBlank()) l.setTitle(req.getTitle().trim());
        if (req.getDescription() != null) l.setDescription(req.getDescription());
        if (req.getPrice() != null) l.setPrice(req.getPrice());
        if (req.getCurrency() != null && req.getCurrency().length() == 3) l.setCurrency(req.getCurrency().toUpperCase());
        if (req.getNegotiable() != null) l.setNegotiable(req.getNegotiable());
        if (req.getCity() != null && !req.getCity().isBlank()) l.setCity(req.getCity().trim());
        if (req.getDistrict() != null) l.setDistrict(req.getDistrict().trim());

        // ---- Car fields ----
        Car c = l.getCar();
        if (c != null) {
            if (req.getYear() != null) c.setYear(req.getYear());
            if (req.getKilometer() != null) c.setKilometer(req.getKilometer());
            if (req.getColor() != null) c.setColor(req.getColor().trim());
            if (req.getEngineVolumeCc() != null) c.setEngineVolumeCc(req.getEngineVolumeCc());
            if (req.getEnginePowerHp() != null) c.setEnginePowerHp(req.getEnginePowerHp());
        }

        listingRepository.save(l);
        return toEditDto(l);
    }

    @Override
    @Transactional
    public void deleteMyListing(Long storeId, Long listingId) {
        Listing l = listingRepository.findById(listingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing not found"));

        requireOwner(l, storeId);
        listingRepository.delete(l);
    }

    private void requireOwner(Listing l, Long storeId) {
        if (l.getStore() == null || l.getStore().getId() == null || !l.getStore().getId().equals(storeId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This listing does not belong to you");
        }
    }

    private StoreListingEditDto toEditDto(Listing l) {
        StoreListingEditDto dto = new StoreListingEditDto();
        dto.setId(l.getId());
        dto.setTitle(l.getTitle());
        dto.setDescription(l.getDescription());
        dto.setPrice(l.getPrice());
        dto.setCurrency(l.getCurrency());
        dto.setNegotiable(l.getNegotiable());
        dto.setCity(l.getCity());
        dto.setDistrict(l.getDistrict());

        if (l.getCar() != null) {
            dto.setCarId(l.getCar().getId());
            dto.setYear(l.getCar().getYear());
            dto.setKilometer(l.getCar().getKilometer());
            dto.setColor(l.getCar().getColor());
            dto.setEngineVolumeCc(l.getCar().getEngineVolumeCc());
            dto.setEnginePowerHp(l.getCar().getEnginePowerHp());
        }
        return dto;
    }
}
