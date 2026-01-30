package com.example.otoportdeneme.services;

import com.example.otoportdeneme.dto_Objects.admin.WalRowDto;
import com.example.otoportdeneme.dto_Requests.WalSearchRequest;

import java.util.List;

public interface WalService {

    void appendAdminHttp(String actorType, Long actorId,
                         String method, String path, String queryString,
                         Integer status, String ip, String userAgent,
                         String requestBody, String responseBody);

    List<WalRowDto> recent(int limit, String sort);

    List<WalRowDto> search(WalSearchRequest req);
}
