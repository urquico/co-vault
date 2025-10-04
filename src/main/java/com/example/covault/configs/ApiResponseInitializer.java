package com.example.covault.configs;

import com.example.covault.dtos.ApiResponse;
import com.example.covault.utils.DBLogsUtility;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApiResponseInitializer {
    private final DBLogsUtility dbLogsUtility;

    @PostConstruct
    public void init() {
        ApiResponse.setLogger(dbLogsUtility);
    }
}