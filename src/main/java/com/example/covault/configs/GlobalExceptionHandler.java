package com.example.covault.configs;

import com.example.covault.dtos.APIResponse;
import com.example.covault.entities.ZZZSystemMessages;
import com.example.covault.exceptions.APIException;
import com.example.covault.utils.DBLogsUtility;
import com.example.covault.utils.ZZZSystemMessagesUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Arrays;

@RequiredArgsConstructor
@ControllerAdvice
public class GlobalExceptionHandler {
    private final ZZZSystemMessagesUtility systemMessageUtility;
    private final DBLogsUtility logger;

    @ExceptionHandler(APIException.class)
    public APIResponse<Object> handleAPIException(APIException ex) {
        ZZZSystemMessages systemMessage = systemMessageUtility.getMessageByKey(ex.getType());

        // Create error logs
        try {
            String[] callerInfo = logger.getCallerInfo();
            String className = callerInfo[0];
            String methodName = callerInfo[1];
            String lineNumber = callerInfo[2];

            logger.createErrorLogs(
                    systemMessage.getMessageType(),
                    className,
                    methodName + " (line " + lineNumber + ")",
                    ex.getMessage(),
                    Arrays.toString(Thread.currentThread().getStackTrace()),
                    ex.getUserId(),
                    ex.getMetadata()
            );
        } catch (Exception e) {
            System.err.println("Error logging failed: " + e.getMessage());
        }

        return new APIResponse<>(
                ex.getType(),
                null,
                systemMessageUtility
        );
    }
}
