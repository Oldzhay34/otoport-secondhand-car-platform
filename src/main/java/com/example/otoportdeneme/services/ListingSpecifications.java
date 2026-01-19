package com.example.otoportdeneme.services;

import com.example.otoportdeneme.Enums.BodyType;
import com.example.otoportdeneme.Enums.FeatureMatchMode;
import com.example.otoportdeneme.Enums.ListingStatus;
import jakarta.persistence.criteria.*;
import com.example.otoportdeneme.models.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public final class ListingSpecifications {

    private ListingSpecifications() {}

    public static Specification<Listing> statusIs(ListingStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Listing> bodyTypeIs(BodyType bodyType) {
        return (root, query, cb) -> {
            if (bodyType == null) return cb.conjunction();
            Join<Listing, Car> carJoin = root.join("car", JoinType.INNER);
            return cb.equal(carJoin.get("bodyType"), bodyType);
        };
    }

    // building kat filtresi = Store.floor (senin Store modelinde floor vardı)
    public static Specification<Listing> storeFloorIs(Integer floor) {
        return (root, query, cb) -> {
            if (floor == null) return cb.conjunction();
            Join<Listing, Store> storeJoin = root.join("store", JoinType.INNER);
            return cb.equal(storeJoin.get("floor"), floor);
        };
    }

    /**
     Feature filtresi:
     - ANY: seçilen featureId'lerden en az biri car_features içinde varsa
     - ALL: seçilen featureId'lerin hepsi car_features içinde varsa

     * Not: CarFeature join entity kullanıyoruz.
     */
    public static Specification<Listing> hasFeatures(List<Long> featureIds, FeatureMatchMode mode) {
        return (root, query, cb) -> {
            if (featureIds == null || featureIds.isEmpty()) return cb.conjunction();

            final FeatureMatchMode matchMode = (mode == null) ? FeatureMatchMode.ANY : mode;

            // query duplicate olmasın
            query.distinct(true);

            Join<Listing, Car> carJoin = root.join("car", JoinType.INNER);

            if (matchMode == FeatureMatchMode.ANY) {
                Join<Car, CarFeature> cfJoin = carJoin.join("carFeatures", JoinType.INNER);
                Join<CarFeature, Feature> fJoin = cfJoin.join("feature", JoinType.INNER);
                return fJoin.get("id").in(featureIds);
            }

            // ALL mode: subquery ile "bu araba seçilen feature sayısı kadar feature'e sahip mi?"
            Subquery<Long> sub = query.subquery(Long.class);
            Root<CarFeature> cf = sub.from(CarFeature.class);
            Join<CarFeature, Feature> f = cf.join("feature", JoinType.INNER);

            sub.select(cb.countDistinct(f.get("id")))
                    .where(
                            cb.equal(cf.get("car"), carJoin),
                            f.get("id").in(featureIds)
                    );

            return cb.equal(sub, (long) featureIds.size());
        };
    }


}
