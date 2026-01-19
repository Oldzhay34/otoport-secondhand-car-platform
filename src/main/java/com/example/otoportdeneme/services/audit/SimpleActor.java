package com.example.otoportdeneme.services.audit;

import com.example.otoportdeneme.Enums.ActorType;
import com.example.otoportdeneme.Interfaces.AuditableActor;

public class SimpleActor implements AuditableActor {

    private final ActorType type;
    private final Long id;

    public SimpleActor(ActorType type, Long id) {
        this.type = type;
        this.id = id;
    }

    @Override
    public ActorType getActorType() {
        return type;
    }

    // ⚠️ AuditableActor interface'inde "getActorId" / "getId" neyse ona göre değişebilir.
    public Long getActorId() {
        return id;
    }

    @Override
    public Long getId() {
        return 0L;
    }
}
