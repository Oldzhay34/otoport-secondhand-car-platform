package com.example.otoportdeneme.services;

import com.example.otoportdeneme.Enums.ActorType;

import java.util.List;

public interface MessageModerationAttemptService {

    void record(ActorType actorType,
                Long actorId,
                Long inquiryId,
                String reason,
                Integer hitCount,
                List<String> matches,
                String ip,
                String userAgent);
}
