package com.example.otoportdeneme.dto_Objects;

import com.example.otoportdeneme.Interfaces.ClientProfileView;

public final class ClientMapper {

    private ClientMapper() {}

    public static ClientProfileDto toDto(ClientProfileView v) {
        if (v == null) return null;

        ClientProfileDto dto = new ClientProfileDto();
        dto.setId(v.getId());
        dto.setFirstName(v.getFirstName());
        dto.setLastName(v.getLastName());
        dto.setEmail(v.getEmail());
        dto.setPhone(v.getPhone());
        dto.setBirthDate(v.getBirthDate());
        dto.setMarketingConsent(v.getMarketingConsent());
        return dto;
    }
}
