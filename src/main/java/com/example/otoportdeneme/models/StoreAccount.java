package com.example.otoportdeneme.models;

import com.example.otoportdeneme.Enums.ActorType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "store_accounts")
public class StoreAccount extends UserAccount {

    @Override
    public ActorType getActorType() {
        return ActorType.STORE;
    }
}
