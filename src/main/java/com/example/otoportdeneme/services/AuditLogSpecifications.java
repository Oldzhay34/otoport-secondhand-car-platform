package com.example.otoportdeneme.services;

import com.example.otoportdeneme.Enums.ActorType;
import com.example.otoportdeneme.models.AuditLog;
import org.springframework.data.jpa.domain.Specification;

public final class AuditLogSpecifications {
    private AuditLogSpecifications() {}

    public static Specification<AuditLog> actorTypeIs(ActorType t){
        return (root, q, cb) -> (t == null) ? cb.conjunction() : cb.equal(root.get("actorType"), t);
    }

    public static Specification<AuditLog> actorIdIs(Long id){
        return (root, q, cb) -> (id == null) ? cb.conjunction() : cb.equal(root.get("actorId"), id);
    }

    public static Specification<AuditLog> actionIs(String action){
        return (root, q, cb) -> {
            if (action == null || action.isBlank()) return cb.conjunction();
            return cb.equal(root.get("action").as(String.class), action.trim().toUpperCase());
        };
    }

    public static Specification<AuditLog> entityTypeIs(String entityType){
        return (root, q, cb) -> {
            if (entityType == null || entityType.isBlank()) return cb.conjunction();
            return cb.equal(cb.upper(root.get("entityType")), entityType.trim().toUpperCase());
        };
    }

    public static Specification<AuditLog> entityIdIs(Long id){
        return (root, q, cb) -> (id == null) ? cb.conjunction() : cb.equal(root.get("entityId"), id);
    }

    public static Specification<AuditLog> detailsLike(String qText){
        return (root, q, cb) -> {
            if (qText == null || qText.isBlank()) return cb.conjunction();
            String like = "%" + qText.trim().toLowerCase() + "%";
            return cb.like(cb.lower(root.get("details")), like);
        };
    }
}
