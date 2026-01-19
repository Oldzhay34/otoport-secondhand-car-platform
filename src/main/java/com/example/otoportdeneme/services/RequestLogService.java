package com.example.otoportdeneme.services;

import com.example.otoportdeneme.models.RequestLog;
import com.example.otoportdeneme.repositories.RequestLogRepository;
import org.springframework.stereotype.Service;

@Service
public class RequestLogService {

    private final RequestLogRepository repo;

    public RequestLogService(RequestLogRepository repo) {
        this.repo = repo;
    }

    public void save(RequestLog log) {
        repo.save(log);
    }
}
