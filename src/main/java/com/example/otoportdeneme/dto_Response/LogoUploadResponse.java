package com.example.otoportdeneme.dto_Response;

public class LogoUploadResponse {
    private String logoUrl;

    public LogoUploadResponse() {}
    public LogoUploadResponse(String logoUrl) { this.logoUrl = logoUrl; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
}
