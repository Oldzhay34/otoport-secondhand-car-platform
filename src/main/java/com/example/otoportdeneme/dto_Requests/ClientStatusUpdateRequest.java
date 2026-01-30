package com.example.otoportdeneme.dto_Requests;

import com.example.otoportdeneme.Enums.AccountStatus;

import java.util.List;

public class ClientStatusUpdateRequest {
    // null gelirse toggle
    private AccountStatus status;

    // toplu işlem için (opsiyonel)
    private List<Long> clientIds;

    public ClientStatusUpdateRequest() {}

    public ClientStatusUpdateRequest(AccountStatus status) {
        this.status = status;
    }

    public AccountStatus getStatus() { return status; }
    public void setStatus(AccountStatus status) { this.status = status; }

    public List<Long> getClientIds() { return clientIds; }
    public void setClientIds(List<Long> clientIds) { this.clientIds = clientIds; }
}
