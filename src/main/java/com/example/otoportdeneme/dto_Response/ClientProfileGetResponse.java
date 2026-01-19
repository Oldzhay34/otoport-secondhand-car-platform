package com.example.otoportdeneme.dto_Response;

import com.example.otoportdeneme.dto_Objects.ClientProfileDto;

public class ClientProfileGetResponse {

    // guest ise false, login ise true
    private boolean authenticated;

    // authenticated=false ise null
    private ClientProfileDto profile;

    public ClientProfileGetResponse() {}

    public ClientProfileGetResponse(boolean authenticated, ClientProfileDto profile) {
        this.authenticated = authenticated;
        this.profile = profile;
    }

    public boolean isAuthenticated() { return authenticated; }
    public void setAuthenticated(boolean authenticated) { this.authenticated = authenticated; }

    public ClientProfileDto getProfile() { return profile; }
    public void setProfile(ClientProfileDto profile) { this.profile = profile; }
}
