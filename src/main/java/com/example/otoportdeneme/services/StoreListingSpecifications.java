package com.example.otoportdeneme.services;

import com.example.otoportdeneme.Enums.BodyType;
import com.example.otoportdeneme.Enums.FeatureMatchMode;
import com.example.otoportdeneme.Enums.ListingStatus;
import jakarta.persistence.criteria.*;
import com.example.otoportdeneme.models.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public final class StoreListingSpecifications {

    private StoreListingSpecifications() {}

    public static Specification<Listing> activeOnly() {
        return (root, query, cb) -> cb.equal(root.get("status"), ListingStatus.ACTIVE);
    }

    public static Specification<Listing> storeIs(Long storeId) {
        return (root, query, cb) -> {
            if (storeId == null) return cb.conjunction();
            Join<Listing, Store> storeJoin = root.join("store", JoinType.INNER);
            return cb.equal(storeJoin.get("id"), storeId);
        };
    }

    public static Specification<Listing> bodyTypeIs(BodyType bodyType) {
        return (root, query, cb) -> {
            if (bodyType == null) return cb.conjunction();
            Join<Listing, Car> carJoin = root.join("car", JoinType.INNER);
            return cb.equal(carJoin.get("bodyType"), bodyType);
        };
    }

    public static Specification<Listing> hasFeatures(List<Long> featureIds, FeatureMatchMode mode) {
        return (root, query, cb) -> {
            if (featureIds == null || featureIds.isEmpty()) return cb.conjunction();

            final FeatureMatchMode matchMode = (mode == null) ? FeatureMatchMode.ANY : mode;

            query.distinct(true);

            Join<Listing, Car> carJoin = root.join("car", JoinType.INNER);

            if (matchMode == FeatureMatchMode.ANY) {
                Join<Car, CarFeature> cfJoin = carJoin.join("carFeatures", JoinType.INNER);
                Join<CarFeature, Feature> fJoin = cfJoin.join("feature", JoinType.INNER);
                return fJoin.get("id").in(featureIds);
            }

            // ALL mode
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
