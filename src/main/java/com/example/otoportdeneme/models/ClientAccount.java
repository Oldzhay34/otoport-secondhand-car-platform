package com.example.otoportdeneme.models;

import com.example.otoportdeneme.Enums.ActorType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "client_accounts")
public class ClientAccount extends UserAccount {

    @Override
    public ActorType getActorType() {
        return ActorType.CLIENT;
    }


}
