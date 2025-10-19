package com.example.covault.configs;

import com.example.covault.entities.Users;
import com.example.covault.repositories.UserRepository;
import com.example.covault.utils.CookieUtility;
import com.example.covault.utils.UserAgentUtility;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class JWTCookieFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;
    private final CookieUtility cookieUtility;
    private final UserRepository userRepository;
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Wrap request to allow multiple reads
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);

        String token = Optional.ofNullable(cookieUtility.readCookieValue(request, "access_token"))
                .orElse("");

        try {
            String userAgent = request.getHeader("User-Agent");
            String domain = request.getHeader("Host");

            // Always attach device info
            request.setAttribute("ipAddress", request.getRemoteAddr());
            request.setAttribute("device", UserAgentUtility.getDevice(userAgent));
            request.setAttribute("domain", domain);
            request.setAttribute("os", UserAgentUtility.getOS(userAgent));
            request.setAttribute("browser", UserAgentUtility.getBrowser(userAgent));
            request.setAttribute("userAgent", UserAgentUtility.getSummary(userAgent));

            // Only parse body when no JWT (e.g., login/register)
            if (token.isEmpty()) {
                // Proceed filter chain first so body is cached
                filterChain.doFilter(wrappedRequest, response);

                String body = new String(wrappedRequest.getContentAsByteArray(), wrappedRequest.getCharacterEncoding());
                if (!body.isEmpty()) {
                    JsonNode jsonNode = mapper.readTree(body);
                    String email = jsonNode.path("email").asText();
                    request.setAttribute("email", email);
                }
                return;
            }

            // With JWT
            String email = cookieUtility.getEmailFromAccessCookie(request);
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);

            Users user = userRepository.findByEmail(email).orElse(null);
            assert user != null;
            request.setAttribute("email", user.getEmail());

        } catch (JwtException e) {
            log.error(e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Invalid or expired JWT token\"}");
            return;
        }

        filterChain.doFilter(wrappedRequest, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return false; // Always run the filter
    }
}
