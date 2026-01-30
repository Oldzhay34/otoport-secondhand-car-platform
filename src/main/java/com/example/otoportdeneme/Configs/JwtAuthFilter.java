package com.example.otoportdeneme.Configs;

import com.example.otoportdeneme.services.CustomUserDetailsService;
import com.example.otoportdeneme.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService uds;

    public JwtAuthFilter(JwtService jwtService, CustomUserDetailsService uds) {
        this.jwtService = jwtService;
        this.uds = uds;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        // ✅ auth endpointleri serbest
        if (path.startsWith("/api/auth/")) return true;

        // ✅ statikler serbest
        return path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/images/")
                || path.startsWith("/imagesforapp/")
                || path.startsWith("/uploads/")
                || path.startsWith("/filejson/")
                || path.startsWith("/templates/")
                || path.equals("/")
                || path.equals("/favicon.ico");
        // ⚠️ DİKKAT: .png/.js/.html gibi suffix bazlı bypassları kaldırdım.
        // Çünkü bazen path farklı encode olunca yanlışlıkla API istekleri de bypass olabiliyor.
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain)
            throws ServletException, IOException {

        String path = req.getRequestURI();
        String authHeader = req.getHeader("Authorization");

        // Token yoksa devam (SecurityConfig karar verir)
        if (authHeader == null || authHeader.isBlank() || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(req, res);
            return;
        }

        String token = authHeader.substring(7).trim();


        try {
            String email = jwtService.parse(token).getBody().getSubject();

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = uds.loadUserByUsername(email);

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                SecurityContextHolder.getContext().setAuthentication(authToken);

                System.out.println("JWT subject(email)=" + email);
                System.out.println("AUTH authorities=" + userDetails.getAuthorities());

            }

            chain.doFilter(req, res);

        } catch (Exception e) {
            e.printStackTrace(); // <-- bunu ekle (şimdilik)
            SecurityContextHolder.clearContext();
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token: " + e.getClass().getSimpleName());
            return;
        }

    }
}
