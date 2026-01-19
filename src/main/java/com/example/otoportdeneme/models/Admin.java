package com.example.otoportdeneme.models;

import com.example.otoportdeneme.Enums.*;

import com.example.otoportdeneme.Interfaces.AuditableActor;
import com.example.otoportdeneme.Interfaces.NotifiableRecipient;
import jakarta.persistence.*;
import com.example.otoportdeneme.Enums.AccountStatus;

@Entity
@Table(name = "admins")
public class Admin extends UserAccount
        implements AuditableActor, NotifiableRecipient {

    @Column(nullable = false)
    private Boolean superAdmin = false;

    @Override
    public ActorType getActorType() {
        return ActorType.ADMIN;
    }

    @Override
    public RecipientType getRecipientType() {
        return RecipientType.ADMIN;
    }

    public Boolean getSuperAdmin() {
        return superAdmin;
    }

    public void setSuperAdmin(Boolean superAdmin) {
        this.superAdmin = superAdmin;
    }
}
