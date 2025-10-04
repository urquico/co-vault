package com.example.covault.dtos;

import com.example.covault.enums.MessageType;
import com.example.covault.enums.SystemMessageType;
import com.example.covault.utils.DBLogsUtility;
import com.example.covault.utils.ZZZSystemMessagesUtility;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Arrays;

@Data
@Setter
@NoArgsConstructor
public class ApiResponse<T> {
    private String message;
    private int status;
    private T data;
    private String timeStamp = Instant.now().toString();

    // reference to logging utility (could be set by Spring later)
    private static DBLogsUtility logger;

    public ApiResponse(SystemMessageType systemMessageType, ZZZSystemMessagesUtility systemMessagesUtility, int status, T data) {
        this.message = systemMessagesUtility.getMessageByKey(systemMessageType).getMessageText();
        this.status = systemMessagesUtility.getMessageByKey(systemMessageType).getStatusCode();
        this.data = data;

        // Check for error-like status
        if (status != 200 && status != 201 && logger != null) {
            try {
                // Get the caller of ApiResponse constructor (skip first few frames)
                StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

                // Index 0 = getStackTrace, 1 = this method, 2 = constructor, 3 = caller
                String className = "UnknownClass";
                String methodName = "UnknownMethod";
                if (stackTrace.length > 3) {
                    StackTraceElement caller = stackTrace[3];
                    className = caller.getClassName();
                    methodName = caller.getMethodName();
                }

                logger.createErrorLogs(
                        MessageType.ERROR,
                        className,
                        methodName,
                        message,
                        Arrays.toString(stackTrace), // full stacktrace as String
                        null, // userId if available
                        null  // extraData
                );
            } catch (Exception e) {
                // Avoid breaking ApiResponse if logging fails
                System.err.println("Error logging failed: " + e.getMessage());
            }
        }
    }
}