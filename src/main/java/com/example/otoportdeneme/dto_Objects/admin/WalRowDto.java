package com.example.otoportdeneme.dto_Objects.admin;

import java.time.Instant;

public class WalRowDto {
    public Long id;
    public Instant createdAt;

    public String actorType;
    public Long actorId;

    public String method;
    public String path;
    public String queryString;

    public Integer status;
    public String ipAddress;
    public String userAgent;

    public String requestBody;
    public String responseBody;

    public String prevHash;
    public String hash;

    public WalRowDto() {}

    public WalRowDto(Long id, Instant createdAt, String actorType, Long actorId,
                     String method, String path, String queryString,
                     Integer status, String ipAddress, String userAgent,
                     String requestBody, String responseBody,
                     String prevHash, String hash) {
        this.id = id;
        this.createdAt = createdAt;
        this.actorType = actorType;
        this.actorId = actorId;
        this.method = method;
        this.path = path;
        this.queryString = queryString;
        this.status = status;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.requestBody = requestBody;
        this.responseBody = responseBody;
        this.prevHash = prevHash;
        this.hash = hash;
    }
}
