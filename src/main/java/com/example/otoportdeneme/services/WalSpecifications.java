package com.example.otoportdeneme.services;

import com.example.otoportdeneme.models.WalEntry;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

public class WalSpecifications {

    public static Specification<WalEntry> actorTypeIs(String actorType) {
        return (root, q, cb) -> actorType == null || actorType.isBlank()
                ? cb.conjunction()
                : cb.equal(root.get("actorType"), actorType.trim());
    }

    public static Specification<WalEntry> actorIdIs(Long actorId) {
        return (root, q, cb) -> actorId == null ? cb.conjunction() : cb.equal(root.get("actorId"), actorId);
    }

    public static Specification<WalEntry> methodIs(String method) {
        return (root, q, cb) -> method == null || method.isBlank()
                ? cb.conjunction()
                : cb.equal(root.get("method"), method.trim().toUpperCase());
    }

    public static Specification<WalEntry> statusIs(Integer status) {
        return (root, q, cb) -> status == null ? cb.conjunction() : cb.equal(root.get("status"), status);
    }

    public static Specification<WalEntry> pathContains(String s) {
        return (root, q, cb) -> s == null || s.isBlank()
                ? cb.conjunction()
                : cb.like(cb.lower(root.get("path")), "%" + s.trim().toLowerCase() + "%");
    }

    public static Specification<WalEntry> createdAtBetween(Instant from, Instant to) {
        return (root, q, cb) -> {
            if (from == null && to == null) return cb.conjunction();
            if (from != null && to != null) return cb.between(root.get("createdAt"), from, to);
            if (from != null) return cb.greaterThanOrEqualTo(root.get("createdAt"), from);
            return cb.lessThanOrEqualTo(root.get("createdAt"), to);
        };
    }

    public static Specification<WalEntry> bodyLike(String qStr) {
        return (root, q, cb) -> {
            if (qStr == null || qStr.isBlank()) return cb.conjunction();
            String like = "%" + qStr.trim().toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("requestBody")), like),
                    cb.like(cb.lower(root.get("responseBody")), like)
            );
        };
    }
}
