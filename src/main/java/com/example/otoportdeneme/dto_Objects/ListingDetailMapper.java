package com.example.otoportdeneme.dto_Objects;

import com.example.otoportdeneme.models.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class ListingDetailMapper {

    private ListingDetailMapper() {}

    public static ListingDetailDto toDto(Listing listing) {
        ListingDetailDto dto = new ListingDetailDto();

        dto.setId(listing.getId());
        dto.setTitle(listing.getTitle());
        dto.setDescription(listing.getDescription());
        dto.setPrice(listing.getPrice());
        dto.setCurrency(listing.getCurrency());
        dto.setNegotiable(listing.getNegotiable());
        dto.setCity(listing.getCity());
        dto.setDistrict(listing.getDistrict());
        dto.setStatus(listing.getStatus());
        dto.setViewCount(listing.getViewCount());
        dto.setFavoriteCount(listing.getFavoriteCount());
        dto.setCreatedAt(listing.getCreatedAt());
        dto.setPublishedAt(listing.getPublishedAt());


        if (listing.getStore() != null) {
            Store s = listing.getStore();
            dto.setStore(new StoreCardDto(
                    s.getId(),
                    s.getStoreName(),
                    s.getCity(),
                    s.getDistrict(),
                    s.getVerified(),
                    s.getFloor(),
                    s.getShopNo(),
                    s.getDirectionNote(),
                    s.getLogoUrl()
            ));
        }

        // Car detail
        if (listing.getCar() != null) {
            Car c = listing.getCar();
            CarDetailDto carDto = new CarDetailDto();
            carDto.setId(c.getId());

            if (c.getBrand() != null) carDto.setBrandName(c.getBrand().getName());
            if (c.getModel() != null) carDto.setModelName(c.getModel().getName());
            carDto.setTrimName(c.getTrimName());

            carDto.setYear(c.getYear());
            carDto.setKilometer(c.getKilometer());
            carDto.setTransmission(c.getTransmission());
            carDto.setFuelType(c.getFuelType());
            carDto.setBodyType(c.getBodyType());
            carDto.setEngineVolumeCc(c.getEngineVolumeCc());
            carDto.setEnginePowerHp(c.getEnginePowerHp());
            carDto.setColor(c.getColor());

            dto.setCar(carDto);


            if (c.getCarFeatures() != null) {
                List<String> names = c.getCarFeatures().stream()
                        .filter(cf -> cf.getFeature() != null)
                        .map(cf -> cf.getFeature().getName())
                        .distinct()
                        .collect(Collectors.toList());
                dto.setFeatures(names);
            }
        }

        // Images
        List<ListingImageDto> imgs = new ArrayList<>();
        if (listing.getImages() != null) {
            listing.getImages().stream()
                    .sorted(Comparator.comparing(ListingImage::getSortOrder))
                    .forEach(img -> imgs.add(new ListingImageDto(
                            img.getId(),
                            img.getImagePath(),
                            img.getSortOrder(),
                            img.getIsCover()
                    )));
        }
        dto.setImages(imgs);

        return dto;
    }
}
