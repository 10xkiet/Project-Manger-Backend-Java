package com.group8.projectmanager.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import com.group8.projectmanager.services.JwtsService;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtsService jwtsService;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final WebAuthenticationDetailsSource authenticationDetailsSource;

    private Optional<String> getTokenFromRequest(HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Optional.empty();
        }

        return Optional.of(authHeader.substring(7));
    }

    private void setAuthenticationInfo(HttpServletRequest request) {

        var token = getTokenFromRequest(request);
        if (token.isEmpty()) {
            return;
        }

        var user = jwtsService
                .getUserFromToken(token.get())
                .orElse(null);

        if (user == null) {
            return;
        }

        var authToken = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities()
        );

        var authDetail = authenticationDetailsSource.buildDetails(request);
        authToken.setDetails(authDetail);

        SecurityContextHolder
                .getContext()
                .setAuthentication(authToken);
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) {
        try {

            setAuthenticationInfo(request);
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            handlerExceptionResolver.resolveException(request, response, null, e);
        }
    }
}