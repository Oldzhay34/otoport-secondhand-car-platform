package com.example.otoportdeneme.Configs;

import com.example.otoportdeneme.services.WalService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

public class AdminWalFilter extends OncePerRequestFilter {

    private final WalService walService;
    private final AdminWalProperties props;

    public AdminWalFilter(WalService walService, AdminWalProperties props) {
        this.walService = walService;
        this.props = props;
    }

    // WAL endpointleri kendini loglamasın
    private static final Set<String> SKIP_PATHS = Set.of(
            "/api/admin/wal/recent",
            "/api/admin/wal/search"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!props.isEnabled()) return true;

        String p = request.getRequestURI();
        if (p == null) return true;

        // sadece admin api
        if (!p.startsWith("/api/admin/")) return true;

        // wal api kendini loglamasın
        if (SKIP_PATHS.contains(p)) return true;

        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        ContentCachingRequestWrapper req = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper res = new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(req, res);
        } finally {
            try {
                // ------- actor resolve -------
                String actorType = "ADMIN";
                Long actorId = null;

                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.isAuthenticated()) {

                    // 1) Custom principal (getId/getActorType)
                    Object principal = auth.getPrincipal();
                    if (principal != null) {
                        Long pid = tryReadLong(principal, "getId");
                        if (pid != null) actorId = pid;

                        String at = tryReadString(principal, "getActorType");
                        if (at != null && !at.isBlank()) actorType = at;
                    }

                    // 2) JwtAuthenticationToken -> claimlerden id/role çek
                    // Reflection ile: compile-time bağımlılık olmasın
                    if (actorId == null && auth.getClass().getName().contains("JwtAuthenticationToken")) {
                        try {
                            Object jwt = auth.getClass().getMethod("getToken").invoke(auth);
                            @SuppressWarnings("unchecked")
                            Map<String, Object> claims =
                                    (Map<String, Object>) jwt.getClass().getMethod("getClaims").invoke(jwt);

                            Object idObj =
                                    firstNonNull(claims.get("id"),
                                            firstNonNull(claims.get("userId"),
                                                    firstNonNull(claims.get("uid"),
                                                            firstNonNull(claims.get("adminId"),
                                                                    claims.get("sub")))));

                            actorId = toLong(idObj);

                            Object atObj = firstNonNull(claims.get("actorType"), claims.get("role"));
                            if (atObj != null && !String.valueOf(atObj).isBlank()) {
                                actorType = String.valueOf(atObj);
                            }
                        } catch (Exception ignore) {}
                    }

                    // 3) authority içinde ADMIN varsa ADMIN kalsın
                    try {
                        boolean isAdmin = auth.getAuthorities().stream()
                                .anyMatch(a -> String.valueOf(a.getAuthority()).toUpperCase().contains("ADMIN"));
                        if (isAdmin) actorType = "ADMIN";
                    } catch (Exception ignore) {}
                }

                // ------- request/response meta -------
                String method = req.getMethod();
                String path = req.getRequestURI();
                String query = req.getQueryString();
                int status = res.getStatus();

                String ip = firstNonBlank(req.getHeader("X-Forwarded-For"), req.getRemoteAddr());
                String ua = req.getHeader("User-Agent");

                String reqBody = null;
                String resBody = null;

                if (props.isCaptureBodies() && isJsonLike(req.getContentType())) {
                    reqBody = toStringSafe(req.getContentAsByteArray(), props.getMaxBodyChars());
                }
                if (props.isCaptureBodies() && isJsonLike(res.getContentType())) {
                    resBody = toStringSafe(res.getContentAsByteArray(), props.getMaxBodyChars());
                }

                walService.appendAdminHttp(
                        actorType,
                        actorId,
                        method,
                        path,
                        query,
                        status,
                        ip,
                        ua,
                        reqBody,
                        resBody
                );

            } catch (Exception ignored) {
                // WAL asla request'i bozmasın
            } finally {
                // response body'yi client'a geri yaz
                res.copyBodyToResponse();
            }
        }
    }

    // ---------------- helpers ----------------

    private static boolean isJsonLike(String ct) {
        if (ct == null) return false;
        ct = ct.toLowerCase();
        return ct.contains(MediaType.APPLICATION_JSON_VALUE.toLowerCase())
                || ct.contains("application/problem+json");
    }

    private static String toStringSafe(byte[] bytes, int maxChars) {
        if (bytes == null || bytes.length == 0) return null;
        String s = new String(bytes, StandardCharsets.UTF_8);
        if (s.length() <= maxChars) return s;
        return s.substring(0, maxChars) + "…";
    }

    private static String firstNonBlank(String a, String b) {
        if (a != null && !a.isBlank()) return a;
        return b;
    }

    private static Object firstNonNull(Object a, Object b) {
        return a != null ? a : b;
    }

    private static Long toLong(Object v) {
        if (v == null) return null;
        if (v instanceof Number n) return n.longValue();
        try { return Long.parseLong(String.valueOf(v)); } catch (Exception e) { return null; }
    }

    private static Long tryReadLong(Object obj, String methodName) {
        try {
            Object v = obj.getClass().getMethod(methodName).invoke(obj);
            if (v instanceof Number n) return n.longValue();
        } catch (Exception ignored) {}
        return null;
    }

    private static String tryReadString(Object obj, String methodName) {
        try {
            Object v = obj.getClass().getMethod(methodName).invoke(obj);
            return v == null ? null : String.valueOf(v);
        } catch (Exception ignored) {}
        return null;
    }
}
