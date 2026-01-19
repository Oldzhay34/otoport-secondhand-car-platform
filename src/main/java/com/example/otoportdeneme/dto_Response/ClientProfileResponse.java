package com.example.otoportdeneme.dto_Response;

import com.example.otoportdeneme.dto_Objects.ClientProfileDto;

public class ClientProfileResponse {
    private ClientProfileDto profile;

    public ClientProfileResponse() {}

    public ClientProfileResponse(ClientProfileDto profile) {
        this.profile = profile;
    }

    public ClientProfileDto getProfile() { return profile; }
    public void setProfile(ClientProfileDto profile) { this.profile = profile; }
}
