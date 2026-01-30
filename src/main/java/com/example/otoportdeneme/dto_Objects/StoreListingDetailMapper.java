package com.example.otoportdeneme.dto_Objects;

import com.example.otoportdeneme.dto_Response.StoreListingDetailResponse;
import com.example.otoportdeneme.models.Car;
import com.example.otoportdeneme.models.Listing;
import com.example.otoportdeneme.models.ListingImage;
import com.example.otoportdeneme.models.Store;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class StoreListingDetailMapper {

    private StoreListingDetailMapper() {}

    public static StoreListingDetailResponse toResponse(Listing l) {
        StoreListingDetailResponse r = new StoreListingDetailResponse();

        r.setId(l.getId());
        r.setTitle(l.getTitle());
        r.setDescription(l.getDescription());

        r.setPrice(l.getPrice());
        r.setCurrency(l.getCurrency());
        r.setNegotiable(l.getNegotiable());

        r.setCity(l.getCity());
        r.setDistrict(l.getDistrict());

        r.setStatus(l.getStatus() != null ? l.getStatus().name() : null);

        r.setViewCount(l.getViewCount());
        // Listing#getFavoriteCount Integer dönüyor (senin modelin öyle)
        r.setFavoriteCount(l.getFavoriteCount() != null ? l.getFavoriteCount().longValue() : 0L);

        r.setCreatedAt(l.getCreatedAt());
        r.setPublishedAt(l.getPublishedAt());

        // store
        if (l.getStore() != null) {
            Store s = l.getStore();
            r.setStore(new StoreSummaryDto(
                    s.getId(),
                    s.getStoreName(),
                    s.getCity(),
                    s.getDistrict(),
                    s.getPhone(),
                    s.getLogoUrl()
            ));
        }

        if (l.getCar() != null) {
            Car c = l.getCar();
            CarSummaryDto cd = new CarSummaryDto();
            cd.setId(c.getId());

            if (c.getBrand() != null) cd.setBrandName(c.getBrand().getName());
            if (c.getModel() != null) cd.setModelName(c.getModel().getName());
            cd.setTrimName(c.getTrimName());

            cd.setYear(c.getYear());
            cd.setKilometer(c.getKilometer());
            cd.setTransmission(c.getTransmission());
            cd.setFuelType(c.getFuelType());
            cd.setBodyType(c.getBodyType());
            cd.setEngineVolumeCc(c.getEngineVolumeCc());
            cd.setEnginePowerHp(c.getEnginePowerHp());
            cd.setColor(c.getColor());

            r.setCar(cd);
        }

        List<ListingImageDto> imgs =
                (l.getImages() == null) ? List.of()
                        : l.getImages().stream()
                        .sorted(Comparator.comparing(ListingImage::getSortOrder,
                                Comparator.nullsLast(Comparator.naturalOrder())))
                        .map(img -> new ListingImageDto(
                                img.getId(),
                                img.getImagePath(),
                                img.getSortOrder(),
                                img.getIsCover()
                        ))
                        .collect(Collectors.toList());

        r.setImages(imgs);

        return r;
    }
}
