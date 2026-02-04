package com.winwin.dataapi.configuration;

/**
 * Service-to-Service Security Filter.
 * * Enforces a pre-shared secret (X-Internal-Token) security policy. This ensures
 * that the Data API only processes requests originating from authorized
 * internal services, effectively isolating it from the public internet.
 */

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class InternalTokenFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();

        if (path.startsWith("/api/transform")) {
            filterChain.doFilter(request, response);
            return;
        }

        String internalToken = request.getHeader("X-Internal-Token");

        if (internalToken == null || !internalToken.equals("my-secret-internal-key")) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("text/plain");
            response.getWriter().write("Forbidden: Invalid Internal Token");
            return;
        }

        filterChain.doFilter(request, response);
    }
}