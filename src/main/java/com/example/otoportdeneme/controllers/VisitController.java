package com.example.otoportdeneme.controllers;

import com.example.otoportdeneme.models.VisitLog;
import com.example.otoportdeneme.repositories.VisitLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/visit")
public class VisitController {

    private final VisitLogRepository visitLogRepository;

    public VisitController(VisitLogRepository visitLogRepository) {
        this.visitLogRepository = visitLogRepository;
    }

    // herkes çağırabilsin
    @PostMapping
    public Map<String, Object> hit(Authentication auth,
                                   HttpServletRequest req,
                                   @RequestParam(required = false) String target,
                                   @RequestParam(required = false) String actorType) {

        VisitLog v = new VisitLog();

        // actorType param gelirse onu bas (ADMINHOME gibi sayfalarda kolay test)
        // gelmezse auth varsa USER say, yoksa GUEST
        String at;
        if (actorType != null && !actorType.isBlank()) at = actorType.trim().toUpperCase();
        else if (auth == null) at = "GUEST";
        else at = "CLIENT"; // burada role çözmek istersen JWT claim’den okuruz

        v.setActorType(at);
        v.setActorId(null);
        v.setTarget(target != null ? target : req.getRequestURI());
        v.setIpAddress(resolveClientIp(req));
        v.setUserAgent(req.getHeader("User-Agent"));

        visitLogRepository.save(v);

        return Map.of("ok", true);
    }

    private String resolveClientIp(HttpServletRequest req) {
        String xff = req.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) return xff.split(",")[0].trim();
        String xri = req.getHeader("X-Real-IP");
        if (xri != null && !xri.isBlank()) return xri.trim();
        return req.getRemoteAddr();
    }
}
