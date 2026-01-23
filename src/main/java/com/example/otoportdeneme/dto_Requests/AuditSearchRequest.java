package com.example.otoportdeneme.dto_Requests;

import com.example.otoportdeneme.Enums.ActorType;

public class AuditSearchRequest {
    private ActorType actorType;
    private Long actorId;
    private String action;     // "UPDATE"
    private String entityType; // "LISTING"
    private Long entityId;
    private String q;          // details içinde arama
    private Integer limit = 100;

    // ✅ yeni: sort (asc/desc)
    private String sort = "desc";

    public ActorType getActorType() { return actorType; }
    public void setActorType(ActorType actorType) { this.actorType = actorType; }

    public Long getActorId() { return actorId; }
    public void setActorId(Long actorId) { this.actorId = actorId; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }

    public Long getEntityId() { return entityId; }
    public void setEntityId(Long entityId) { this.entityId = entityId; }

    public String getQ() { return q; }
    public void setQ(String q) { this.q = q; }

    public Integer getLimit() { return limit; }
    public void setLimit(Integer limit) { this.limit = limit; }

    public String getSort() { return sort; }

    // ✅ BOŞ DEĞİL: normalize et
    public void setSort(String sort) {
        if (sort == null || sort.isBlank()) {
            this.sort = "desc";
            return;
        }
        String s = sort.trim().toLowerCase();
        this.sort = (s.equals("asc") ? "asc" : "desc");
    }
}
