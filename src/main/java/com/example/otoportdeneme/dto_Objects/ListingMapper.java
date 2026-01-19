package com.example.otoportdeneme.dto_Objects;

import com.example.otoportdeneme.models.*;

public final class ListingMapper {

    private ListingMapper() {}

    public static ListingCardDto toCardDto(Listing listing) {
        if (listing == null) return null;

        ListingCardDto dto = new ListingCardDto();

        dto.setId(listing.getId());
        dto.setTitle(listing.getTitle());
        dto.setPrice(listing.getPrice());
        dto.setCurrency(listing.getCurrency());
        dto.setCity(listing.getCity());
        dto.setDistrict(listing.getDistrict()); // varsa

        // ✅ store
        if (listing.getStore() != null) {
            dto.setStoreId(listing.getStore().getId());

        }

        // ✅ car
        Car car = listing.getCar();
        if (car != null) {
            dto.setYear(car.getYear());
            dto.setKilometer(car.getKilometer());

            if (car.getBodyType() != null) dto.setBodyType(car.getBodyType().name());
            if (car.getFuelType() != null) dto.setFuelType(car.getFuelType().name());
            if (car.getTransmission() != null) dto.setTransmission(car.getTransmission().name());

            // ✅ engine/model/brand trim üzerinden
            Trim trim = car.getTrim();
            if (trim != null) {
                dto.setEngine(trim.getName());

                CarModel model = trim.getModel();
                if (model != null) {
                    dto.setModel(model.getName());

                    Brand brand = model.getBrand();
                    if (brand != null) dto.setBrand(brand.getName());
                }
            }
        }

        // ✅ cover
        dto.setCoverImageUrl(listing.getCoverImageUrl());
        // Eğer sende coverImageUrl transient getter yoksa:
        // dto.setCoverImageUrl(coverPickedFromImages);

        return dto;
    }


    private static String pickImageUrl(ListingImage img) {
        if (img == null) return null;

        // Sende hangisi varsa onu bırak.
        // Örn: img.getUrl() varsa onu kullan.
        if (img.getUrl() != null && !img.getUrl().isBlank()) return img.getUrl();
        if (img.getImagePath() != null && !img.getImagePath().isBlank()) return img.getImagePath();

        return null;
    }
}
