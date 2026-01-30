package com.example.otoportdeneme.dto_Objects;

import java.util.List;

public class ModerationResult {
    private boolean allowed;
    private String reason;
    private int hitCount;
    private List<String> matches;

    public static ModerationResult ok() {
        ModerationResult r = new ModerationResult();
        r.allowed = true;
        return r;
    }

    public static ModerationResult block(String reason, int hitCount, List<String> matches) {
        ModerationResult r = new ModerationResult();
        r.allowed = false;
        r.reason = reason;
        r.hitCount = hitCount;
        r.matches = matches;
        return r;
    }

    public boolean isAllowed() { return allowed; }
    public String getReason() { return reason; }
    public int getHitCount() { return hitCount; }
    public List<String> getMatches() { return matches; }
}
