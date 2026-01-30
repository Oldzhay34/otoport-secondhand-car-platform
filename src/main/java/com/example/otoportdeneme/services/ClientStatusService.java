package com.example.otoportdeneme.services;

import com.example.otoportdeneme.Enums.AccountStatus;
import com.example.otoportdeneme.dto_Objects.admin.ClientStatusDto;

import java.util.List;

public interface ClientStatusService {
    List<ClientStatusDto> listAllClients();
    ClientStatusDto setClientStatus(Long clientId, AccountStatus status); // null => toggle
    List<ClientStatusDto> bulkSetStatus(List<Long> clientIds, AccountStatus status);
}
