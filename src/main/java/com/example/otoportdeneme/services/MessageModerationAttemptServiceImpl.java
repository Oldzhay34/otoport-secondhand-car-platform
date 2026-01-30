package com.example.otoportdeneme.services;

import com.example.otoportdeneme.Enums.ActorType;
import com.example.otoportdeneme.models.MessageModerationAttempt;
import com.example.otoportdeneme.repositories.MessageModerationAttemptRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MessageModerationAttemptServiceImpl implements MessageModerationAttemptService {

    private final MessageModerationAttemptRepository attemptRepo;

    // ✅ constructor adı class ile aynı olmalı
    public MessageModerationAttemptServiceImpl(MessageModerationAttemptRepository attemptRepo) {
        this.attemptRepo = attemptRepo;
    }

    /**
     * ✅ Ana transaction rollback olsa bile attempt kaydı kalsın diye REQUIRES_NEW
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(ActorType actorType,
                       Long actorId,
                       Long inquiryId,
                       String reason,
                       Integer hitCount,
                       List<String> matches,
                       String ip,
                       String userAgent) {

        MessageModerationAttempt a = new MessageModerationAttempt();
        a.setActorType(actorType);
        a.setActorId(actorId);
        a.setInquiryId(inquiryId);
        a.setReason(reason);
        a.setHitCount(hitCount != null ? hitCount : 0);
        a.setMatchedPreview(String.join(",", matches == null ? List.of() : matches));
        a.setIpAddress(ip);
        a.setUserAgent(userAgent);

        attemptRepo.save(a);
    }
}
