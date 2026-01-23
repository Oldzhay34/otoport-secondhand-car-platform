package com.example.otoportdeneme.services;

import com.example.otoportdeneme.Enums.RecipientType;
import com.example.otoportdeneme.Interfaces.NotifiableRecipient;

public class RecipientRef implements NotifiableRecipient {

    private final Long id;
    private final RecipientType recipientType;

    public RecipientRef(Long id, RecipientType recipientType) {
        this.id = id;
        this.recipientType = recipientType;
    }

    @Override
    public Long getId() { return id; }

    @Override
    public RecipientType getRecipientType() { return recipientType; }
}
