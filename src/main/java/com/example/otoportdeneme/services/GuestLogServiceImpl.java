package com.example.otoportdeneme.services;

import com.example.otoportdeneme.models.GuestAccessLog;
import com.example.otoportdeneme.repositories.GuestAccessLogRepository;
import org.springframework.stereotype.Service;

@Service
public class GuestLogServiceImpl implements GuestLogService {
    private final GuestAccessLogRepository repo;

    public GuestLogServiceImpl(GuestAccessLogRepository repo) { this.repo = repo; }

    @Override
    public void log(String accessType, String target, String ip, String userAgent) {
        GuestAccessLog g = new GuestAccessLog();
        g.setAccessType(accessType);
        g.setTarget(target);
        g.setIpAddress(ip);
        g.setUserAgent(userAgent);
        repo.save(g);
    }
}
