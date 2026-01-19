package com.example.otoportdeneme.services;

import com.example.otoportdeneme.Enums.ActorType;
import com.example.otoportdeneme.Enums.AuditAction;
import com.example.otoportdeneme.Interfaces.AuditableActor;

public interface AuditService {

    void log(AuditableActor actor,
             AuditAction action,
             String entityType,
             Long entityId,
             String details,
             String ip,
             String userAgent);

    void log(ActorType actorType,
             Long actorId,
             AuditAction action,
             String entityType,
             Long entityId,
             String details,
             String ip,
             String userAgent);
}
