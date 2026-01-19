package com.example.otoportdeneme.dto_Response;

import com.example.otoportdeneme.dto_Objects.StoreProfileDto;

public class StoreProfileResponse {
    private StoreProfileDto profile;

    public StoreProfileResponse() {}

    public StoreProfileResponse(StoreProfileDto profile) {
        this.profile = profile;
    }

    public StoreProfileDto getProfile() { return profile; }
    public void setProfile(StoreProfileDto profile) { this.profile = profile; }
}
