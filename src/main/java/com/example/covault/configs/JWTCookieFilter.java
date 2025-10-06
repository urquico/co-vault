package com.example.covault.configs;

import com.example.covault.entities.Users;
import com.example.covault.exceptions.InvalidJWTException;
import com.example.covault.repositories.UserRepository;
import com.example.covault.utils.CookieUtility;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class JWTCookieFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;
    private final CookieUtility cookieUtility;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = cookieUtility.readCookieValue(request, "access_token");
        if (token != null) {
            try {
                Long userId = cookieUtility.getUserIdFromAccessCookie(request);
                UserDetails userDetails = userDetailsService.loadUserByUsername(String.valueOf(userId));
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(auth);

                Users user = userRepository.findById(userId).orElse(null);
                request.setAttribute("user", user);
            } catch (JwtException e) {
                // Unauthenticated
                log.error(e.getMessage());
                throw new InvalidJWTException("Invalid or expired JWT token");
            }
        }
        filterChain.doFilter(request, response);
    }
}
