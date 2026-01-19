package com.example.otoportdeneme.services;

import com.example.otoportdeneme.Enums.ActorType;
import com.example.otoportdeneme.Enums.AuditAction;
import com.example.otoportdeneme.Interfaces.AuditableActor;
import com.example.otoportdeneme.models.AuditLog;
import com.example.otoportdeneme.repositories.AuditLogRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    // ===============================
    // 1️⃣ AuditableActor kullanan log
    // ===============================
    @Override
    @Transactional
    public void log(AuditableActor actor,
                    AuditAction action,
                    String entityType,
                    Long entityId,
                    String details,
                    String ip,
                    String userAgent) {

        if (actor == null) {
            throw new IllegalArgumentException("actor null olamaz");
        }

        log(
                actor.getActorType(),
                actor.getId(),
                action,
                entityType,
                entityId,
                details,
                ip,
                userAgent
        );
    }

    // ==================================
    // 2️⃣ Asıl kullanılan CORE log metodu
    // ==================================
    @Override
    @Transactional
    public void log(ActorType actorType,
                    Long actorId,
                    AuditAction action,
                    String entityType,
                    Long entityId,
                    String details,
                    String ip,
                    String userAgent) {

        AuditLog log = new AuditLog();
        log.setActorType(actorType);
        log.setActorId(actorId);
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setDetails(details);
        log.setIpAddress(ip);
        log.setUserAgent(userAgent);

        auditLogRepository.save(log);
    }
}
