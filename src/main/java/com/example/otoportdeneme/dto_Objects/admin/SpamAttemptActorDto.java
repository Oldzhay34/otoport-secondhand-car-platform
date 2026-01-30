package com.example.otoportdeneme.dto_Objects.admin;

public class SpamAttemptActorDto {
    private String actorType;
    private Long actorId;
    private Long attempts;

    public SpamAttemptActorDto(String actorType, Long actorId, Long attempts) {
        this.actorType = actorType;
        this.actorId = actorId;
        this.attempts = attempts;
    }

    public String getActorType() { return actorType; }
    public Long getActorId() { return actorId; }
    public Long getAttempts() { return attempts; }
}
