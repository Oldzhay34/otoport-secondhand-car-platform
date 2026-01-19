package com.example.otoportdeneme.Configs;

import com.example.otoportdeneme.models.RequestLog;
import com.example.otoportdeneme.services.RequestLogService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class RequestLogFilter extends OncePerRequestFilter {

    private final RequestLogService requestLogService;

    public RequestLogFilter(RequestLogService requestLogService) {
        this.requestLogService = requestLogService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        // static + auth hariç, API’leri loglayalım
        if (path.startsWith("/css/") || path.startsWith("/js/") || path.startsWith("/images/")
                || path.startsWith("/imagesforapp/") || path.startsWith("/uploads/")
                || path.endsWith(".html") || path.endsWith(".css") || path.endsWith(".js")
                || path.endsWith(".png") || path.endsWith(".jpg") || path.endsWith(".jpeg")
                || path.endsWith(".webp") || path.endsWith(".svg") || path.equals("/favicon.ico")
        ) return true;

        return path.startsWith("/api/auth/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            RequestLog log = new RequestLog();
            log.setPath(req.getRequestURI());
            log.setMethod(req.getMethod());
            log.setIpAddress(req.getRemoteAddr());
            log.setUserAgent(req.getHeader("User-Agent"));

            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                log.setIsGuest(false);

                // CustomUserDetailsService'in UserDetails'i içinde id/role varsa buradan çekersin.
                // Şimdilik: role’ü authority’den alalım
                String role = auth.getAuthorities().stream().findFirst().map(a -> a.getAuthority()).orElse("UNKNOWN");
                // ROLE_ADMIN gibi gelirse sadeleştir
                role = role.replace("ROLE_", "");
                log.setRole(role);

                // userId: Eğer principal içinde id yoksa null kalır (yine de istatistik çalışır)
                // ileride CustomUserDetails'e getId() ekleyip burada set edebilirsin.
            } else {
                log.setIsGuest(true);
                log.setRole("GUEST");
            }

            requestLogService.save(log);

        } catch (Exception ignored) {
            // loglama hata verirse request bozulmasın
        }

        chain.doFilter(req, res);
    }
}
