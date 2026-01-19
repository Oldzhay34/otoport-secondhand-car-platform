package com.example.otoportdeneme.Interfaces;

import com.example.otoportdeneme.Enums.ActorType;

public interface AuditableActor extends Identifiable {
    ActorType getActorType();
}
