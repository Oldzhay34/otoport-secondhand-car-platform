package com.example.otoportdeneme.Interfaces;

import java.time.LocalDate;

public interface ClientProfileView {
    Long getId();
    String getFirstName();
    String getLastName();
    String getEmail();
    String getPhone();
    LocalDate getBirthDate();
    Boolean getMarketingConsent();
}
