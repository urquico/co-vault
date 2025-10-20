package com.example.covault.controllers;

import com.example.covault.dtos.APIResponse;
import com.example.covault.dtos.auth.LoginRequestDto;
import com.example.covault.entities.RefreshTokens;
import com.example.covault.entities.RefreshTokensId;
import com.example.covault.entities.Users;
import com.example.covault.enums.ActivityType;
import com.example.covault.enums.SystemMessageType;
import com.example.covault.exceptions.APIException;
import com.example.covault.repositories.RefreshTokenRepository;
import com.example.covault.repositories.UserRepository;
import com.example.covault.utils.CookieUtility;
import com.example.covault.utils.JWTUtility;
import com.example.covault.utils.ZZZCacheMessagesUtility;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final JWTUtility jwtUtility;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final CookieUtility cookieUtility;
    private final ZZZCacheMessagesUtility cacheMessagesUtility;
    private final PasswordEncoder passwordEncoder;

//    @PostMapping("register")

    @PostMapping("/login")
    public APIResponse<Void> login(
            @RequestBody LoginRequestDto req,
            @RequestAttribute("device") String device,
            HttpServletResponse response,
            HttpServletRequest request
    ) {
        try {
            request.setAttribute("email", req.getEmail());

            Users user = userRepository.findByEmail(req.getEmail()).orElse(null);
            if (user == null)
                throw new APIException(
                        SystemMessageType.USER_NOT_FOUND,
                        Map.of("req", req, "device", device)
                );

            if (!passwordEncoder.matches(req.getPassword(), user.getPassword()))
                throw new APIException(
                        SystemMessageType.INCORRECT_PASSWORD,
                        Map.of("req", req, "device", device)
                );

            String accessToken = jwtUtility.generateAccessToken(user.getEmail());
            String refreshToken = jwtUtility.generateRefreshToken();

            // Find existing token
            RefreshTokens refreshTokens = refreshTokenRepository.findById_EmailAndId_Device(user.getEmail(), device).orElse(null);
            Instant now = Instant.now();

            // Upsert token
            if (refreshTokens == null) {
                refreshTokens = new RefreshTokens();
                RefreshTokensId refreshTokenId = new RefreshTokensId();

                refreshTokenId.setEmail(user.getEmail());
                refreshTokenId.setDevice(device);

                refreshTokens.setId(refreshTokenId);
                refreshTokens.setCreatedAt(now);
            }

            refreshTokens.setTokenHash(refreshToken);
            refreshTokens.setExpiresAt(now.plus(1, ChronoUnit.DAYS));
            refreshTokens.setUpdatedAt(now);

            refreshTokenRepository.save(refreshTokens);

            ResponseCookie accessCookie = cookieUtility.createHttpOnlyCookie("access_token", accessToken, 15 * 60);
            ResponseCookie refreshCookie = cookieUtility.createHttpOnlyCookie("refresh_token", refreshToken, 1 * 24 * 60 * 60);

            response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
            response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

            return new APIResponse<>(SystemMessageType.SUCCESS, null, ActivityType.LOGIN);
        } catch (APIException e) {
            throw new APIException(
                    e.getMessage(),
                    Map.of("req", req, "errorMessage", e.getMessage(), "device", device)
            );
        }
    }

    @PostMapping("/refresh")
    public APIResponse<Void> refresh(
            @RequestAttribute("user") Users user,
            HttpServletRequest request,
            HttpServletResponse response) {
        try {
            if (user == null) {
                throw new APIException(
                        SystemMessageType.USER_NOT_FOUND,
                        null
                );
            }

            String refreshToken = cookieUtility.readCookieValue(request, "refresh_token");
            if (refreshToken == null) {
                throw new APIException(
                        SystemMessageType.TOKEN_NOT_FOUND,
                        Map.of("user", user)
                );
            }

            // Find stored token for user and verify match
            RefreshTokens stored = refreshTokenRepository.findByTokenHash(refreshToken).orElse(null);
            if (stored == null || stored.getExpiresAt().isBefore(Instant.now())) {
                throw new APIException(
                        SystemMessageType.TOKEN_NOT_FOUND_OR_EXPIRED,
                        Map.of("user", user)
                );
            }

            // Rotate tokens
            String newAccess = jwtUtility.generateAccessToken(user.getEmail());
            String newRefresh = jwtUtility.generateRefreshToken();
            Instant now = Instant.now();

            stored.setTokenHash(newRefresh); // store directly or hashed
            stored.setExpiresAt(now.plus(1, ChronoUnit.DAYS));
            stored.setUpdatedAt(now);
            refreshTokenRepository.save(stored);

            response.addHeader(HttpHeaders.SET_COOKIE,
                    cookieUtility.createHttpOnlyCookie("access_token", newAccess, 15 * 60).toString());
            response.addHeader(HttpHeaders.SET_COOKIE,
                    cookieUtility.createHttpOnlyCookie("refresh_token", newRefresh, 30 * 24 * 60 * 60).toString());

            return new APIResponse<>(SystemMessageType.SUCCESS, null, null);
        } catch (Exception e) {
            throw new APIException(
                    SystemMessageType.SERVER_ERROR,
                    Map.of("req", request, "errorMessage", e.getMessage(), "user", user)
            );
        }
    }


    @PostMapping("/logout")
    public APIResponse<Void> logout(
            @RequestAttribute("user") Users user,
            @RequestAttribute("device") String device,
            HttpServletRequest request,
            HttpServletResponse response) {
        try {
            if (user.getEmail() != null) {
                refreshTokenRepository.deleteById_EmailAndId_Device(user.getEmail(), device);
            }

            // Clear cookies
            ResponseCookie clearAccess = ResponseCookie.from("access_token", "").httpOnly(true).path("/").maxAge(0).build();
            ResponseCookie clearRefresh = ResponseCookie.from("refresh_token", "").httpOnly(true).path("/").maxAge(0).build();

            response.addHeader(HttpHeaders.SET_COOKIE, clearAccess.toString());
            response.addHeader(HttpHeaders.SET_COOKIE, clearRefresh.toString());

            return new APIResponse<>(SystemMessageType.SUCCESS, null, ActivityType.LOGOUT);
        } catch (Exception e) {
            throw new APIException(
                    SystemMessageType.SERVER_ERROR,
                    Map.of("user", user, "req", request, "errorMessage", e.getMessage(), "device", device)
            );
        }
    }
}
