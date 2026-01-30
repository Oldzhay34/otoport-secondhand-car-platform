package com.example.otoportdeneme.Interfaces;

import com.example.otoportdeneme.Enums.AccountStatus;

public interface ClientStatusView {
    Long getId();
    String getFirstName();
    String getLastName();
    String getEmail();
    AccountStatus getStatus();
}
