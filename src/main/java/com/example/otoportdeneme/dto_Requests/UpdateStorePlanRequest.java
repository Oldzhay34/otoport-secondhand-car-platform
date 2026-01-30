package com.example.otoportdeneme.dto_Requests;

import jakarta.validation.constraints.NotBlank;

public class UpdateStorePlanRequest {
    @NotBlank
    private String plan;

    public String getPlan() { return plan; }
    public void setPlan(String plan) { this.plan = plan; }
}
