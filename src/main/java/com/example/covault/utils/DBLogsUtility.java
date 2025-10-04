package com.example.covault.utils;

import com.example.covault.entities.ZZZErrorLogs;
import com.example.covault.enums.MessageType;
import com.example.covault.repositories.ZZZErrorLogsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DBLogsUtility {

    private final ZZZErrorLogsRepository errorLogsRepository;

    public void createErrorLogs(
            MessageType errorType,
            String className,
            String methodName,
            String message,
            String stackTrace,
            Long userId,
            String extraData
    ) {
        ZZZErrorLogs log = new ZZZErrorLogs();
        log.setTimestamp(LocalDateTime.now());
        log.setErrorType(errorType);
        log.setClassName(className);
        log.setMethodName(methodName);
        log.setMessage(message);
        log.setStackTrace(stackTrace);
        log.setUserId(userId);
        log.setExtraData(extraData);

        errorLogsRepository.save(log);
    }


}