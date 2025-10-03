package com.example.covault.controllers;

import com.example.covault.dtos.ApiResponse;
import com.example.covault.dtos.auth.LoginRequestDto;
import com.example.covault.entities.RefreshTokens;
import com.example.covault.entities.Users;
import com.example.covault.repositories.RefreshTokenRepository;
import com.example.covault.repositories.UserRepository;
import com.example.covault.utils.CookieUtility;
import com.example.covault.utils.JWTUtility;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final JWTUtility jwtUtility;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final CookieUtility cookieUtility;

    @PostMapping("/login")
    public ApiResponse<Object> login(@RequestBody LoginRequestDto req, HttpServletResponse response) {
        Users user = userRepository.findByEmail(req.getEmail()).orElse(null);
        if (user == null)
            return new ApiResponse<>("User not found", HttpStatus.OK, null);

        // Delete all refresh tokens
        refreshTokenRepository.deleteByUserId(user.getId());

        String accessToken = jwtUtility.generateAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtUtility.generateRefreshToken();

        //Store hashed refresh token
        RefreshTokens refreshTokens = new RefreshTokens();
        refreshTokens.setUserId(user.getId());
        refreshTokens.setTokenHash(refreshToken);
        refreshTokens.setExpiresAt(Instant.now().plus(1, ChronoUnit.DAYS));
        refreshTokenRepository.save(refreshTokens);

        ResponseCookie accessCookie = cookieUtility.createHttpOnlyCookie("access_token", accessToken, 15 * 60);
        ResponseCookie refreshCookie = cookieUtility.createHttpOnlyCookie("refresh_token", refreshToken, 1 * 24 * 60 * 60);

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return new ApiResponse<>("Success", HttpStatus.OK, null);
    }

    @PostMapping("/refresh")
    public ApiResponse<Object> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = cookieUtility.readCookieValue(request, "refresh_token");
        if (refreshToken == null) {
            return new ApiResponse<>("No Refresh Token found", HttpStatus.OK, null);
        }

        // Find stored token for user and verify match
        RefreshTokens stored = refreshTokenRepository.findByTokenHash(refreshToken).orElse(null);
        if (stored == null || stored.getExpiresAt().isBefore(Instant.now())) {
            return new ApiResponse<>("UNAUTHORIZED", HttpStatus.OK, null);
        }

        Long userId = stored.getUserId();

        Users user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ApiResponse<>("User not found", HttpStatus.OK, null);
        }

        // Rotate tokens
        String newAccess = jwtUtility.generateAccessToken(user.getId(), user.getEmail());
        String newRefresh = jwtUtility.generateRefreshToken();

        stored.setTokenHash(newRefresh); // store directly or hashed
        stored.setExpiresAt(Instant.now().plus(30, ChronoUnit.DAYS));
        refreshTokenRepository.save(stored);

        response.addHeader(HttpHeaders.SET_COOKIE,
                cookieUtility.createHttpOnlyCookie("access_token", newAccess, 15 * 60).toString());
        response.addHeader(HttpHeaders.SET_COOKIE,
                cookieUtility.createHttpOnlyCookie("refresh_token", newRefresh, 30 * 24 * 60 * 60).toString());

        return new ApiResponse<>("SUCCESS", HttpStatus.OK, null);
    }


    @PostMapping("/logout")
    public ApiResponse<Object> logout(HttpServletRequest request, HttpServletResponse response) {
        Long userId = cookieUtility.getUserIdFromAccessCookie(request);
        if (userId != null) {
            refreshTokenRepository.deleteByUserId(userId);
        }

        // Clear cookies
        ResponseCookie clearAccess = ResponseCookie.from("access_token", "").httpOnly(true).path("/").maxAge(0).build();
        ResponseCookie clearRefresh = ResponseCookie.from("refresh_token", "").httpOnly(true).path("/").maxAge(0).build();

        response.addHeader(HttpHeaders.SET_COOKIE, clearAccess.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, clearRefresh.toString());

        return new ApiResponse<>("SUCCESS", HttpStatus.OK, null);
    }
}
