package com.example.otoportdeneme.services;

import com.example.otoportdeneme.Interfaces.ClientProfileView;

import java.time.LocalDate;

public interface ClientProfileUpdateService {

    ClientProfileView getMyProfileOrThrow(Long clientId);

    ClientProfileView updateMyProfile(
            Long clientId,
            String firstName,
            String lastName,
            String phone,
            LocalDate birthDate,
            Boolean marketingConsent
    );
}
