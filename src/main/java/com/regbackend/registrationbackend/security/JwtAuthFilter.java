package com.regbackend.registrationbackend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // ✅ Step 1: Clean and extract token safely
        if (authHeader != null) {
            // Remove ALL occurrences of "Bearer" or "bearer" and whitespace
            token = authHeader.replaceAll("(?i)bearer", "").replaceAll("\\s+", "");

            System.out.println("Raw Authorization Header: [" + authHeader + "]");
            System.out.println("Cleaned Token: [" + token + "] length=" + token.length());
        }

        // ✅ Step 2: Parse and validate
        if (token != null && token.matches("^[A-Za-z0-9-_\\.]+$")) {
            try {
                username = jwtService.extractUsername(token);
            } catch (Exception e) {
                System.out.println("❌ JWT parsing error: " + e.getMessage());
            }
        } else if (token != null) {
            System.out.println("⚠️ Token contains invalid characters, skipping authentication");
        }

        // ✅ Step 3: Authenticate user if valid
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtService.isTokenValid(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
