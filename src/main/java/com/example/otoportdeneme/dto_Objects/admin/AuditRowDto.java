package com.example.otoportdeneme.dto_Objects.admin;

import java.time.Instant;

public class AuditRowDto {
    private Instant createdAt;
    private String actorType;
    private Long actorId;
    private String action;
    private String entityType;
    private Long entityId;
    private String details;

    public AuditRowDto() {}

    public AuditRowDto(Instant createdAt, String actorType, Long actorId, String action,
                       String entityType, Long entityId, String details) {
        this.createdAt = createdAt;
        this.actorType = actorType;
        this.actorId = actorId;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.details = details;
    }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public String getActorType() { return actorType; }
    public void setActorType(String actorType) { this.actorType = actorType; }

    public Long getActorId() { return actorId; }
    public void setActorId(Long actorId) { this.actorId = actorId; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }

    public Long getEntityId() { return entityId; }
    public void setEntityId(Long entityId) { this.entityId = entityId; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}
