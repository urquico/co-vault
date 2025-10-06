package com.example.covault.configs;

import com.example.covault.repositories.UserRepository;
import com.example.covault.utils.CookieUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {
    private final UserRepository userRepository;
    private final CookieUtility cookieUtility;
    private final CustomAuthEntryPoint customAuthEntryPoint;

    @Bean
    public UserDetailsService userDetailsService() {
        return userIdString -> {
            Long userId;
            try {
                userId = Long.parseLong(userIdString);
            } catch (NumberFormatException e) {
                throw new UsernameNotFoundException("Invalid user ID: " + userIdString);
            }

            return userRepository.findById(userId)
                    .map(user -> User.withUsername(user.getEmail()) // username here can be email
                            .password("{noop}") // no password stored yet
                            .roles("USER")
                            .build())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userId));
        };
    }

    @Bean
    public JWTCookieFilter jwtCookieFilter(UserDetailsService userDetailsService) {
        return new JWTCookieFilter(userDetailsService, cookieUtility, userRepository);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JWTCookieFilter jwtCookieFilter) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/login", "/api/v1/auth/register").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(customAuthEntryPoint)
                )
                .addFilterBefore(jwtCookieFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
