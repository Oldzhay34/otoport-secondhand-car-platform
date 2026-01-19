package com.example.otoportdeneme.Interfaces;

import com.example.otoportdeneme.Enums.AuditAction;
import com.example.otoportdeneme.models.AuditLog;

public class AuditLogFactory {

    public static AuditLog create(AuditableActor actor,
                                  AuditAction action,
                                  String entityType,
                                  Long entityId,
                                  String details,
                                  String ip,
                                  String userAgent) {

        AuditLog a = new AuditLog();
        a.setActorType(actor.getActorType());

        // ⚠️ burası actorId nasıl alınıyorsa ona göre:
        // a.setActorId(actor.getId());
        // veya:
        // a.setActorId(((SimpleActor)actor).getActorId());

        a.setAction(action);
        a.setEntityType(entityType);
        a.setEntityId(entityId);
        a.setDetails(details);
        a.setIpAddress(ip);
        a.setUserAgent(userAgent);
        return a;
    }
}
