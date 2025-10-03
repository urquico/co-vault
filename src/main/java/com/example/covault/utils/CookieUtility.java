package com.example.covault.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtility {
    private final String secretKey;

    public CookieUtility(@Value("${spring.application.jwt.secret}") String secretKey) {
        this.secretKey = secretKey;
    }

    public ResponseCookie createHttpOnlyCookie(String name, String value, long maxAgeSeconds) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(true) // true in prod (https)
                .path("/")
                .maxAge(maxAgeSeconds)
                .sameSite("Strict") // or "Lax" or "None" depending on needs
                .build();
    }

    public String readCookieValue(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;

        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(name)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public Long getUserIdFromAccessCookie(HttpServletRequest request) {
        String token = readCookieValue(request, "access_token");
        if (token == null) return null;

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return Long.parseLong(claims.getSubject()); // now safe
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
