package com.example.otoportdeneme.services;

import com.example.otoportdeneme.Interfaces.ClientProfileView;

import java.util.Optional;

public interface ClientProfileService {

    /**
     * @param clientId logged-in client id (null ise guest)
     * @return client profile view (guest ise empty)
     */
    Optional<ClientProfileView> getMyProfile(
            Long clientId,
            String ip,
            String userAgent
    );
}
