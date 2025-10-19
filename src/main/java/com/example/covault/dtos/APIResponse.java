package com.example.covault.dtos;

import com.example.covault.entities.ZZZActivityMessages;
import com.example.covault.entities.ZZZSystemMessages;
import com.example.covault.enums.ActivityType;
import com.example.covault.enums.SystemMessageType;
import com.example.covault.utils.DBLogsUtility;
import com.example.covault.utils.ZZZCacheMessagesUtility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Data
@NoArgsConstructor
public class APIResponse<T> {
    private String message;
    private int status;
    private T data;
    private String timeStamp = Instant.now().toString();

    @Setter
    @JsonIgnore
    private static DBLogsUtility dbLogsUtility;

    @Setter
    @JsonIgnore
    private static ZZZCacheMessagesUtility cacheMessagesUtility;

    // Success Response
    public APIResponse(SystemMessageType systemMessageType, T data, ActivityType activityType) {
        this(systemMessageType, data, activityType, null, null);
    }

    public APIResponse(SystemMessageType systemMessageType, T data, ActivityType activityType, String oldData, String newData) {
        ZZZSystemMessages systemMessage = cacheMessagesUtility.getSystemMessageByKey(systemMessageType);

        if (activityType != null) {
            ZZZActivityMessages activityMessage = cacheMessagesUtility.getActivityMessageByKey(activityType);
            dbLogsUtility.createActivityLogs(activityMessage.getMessageText(), oldData, newData);
        }

        this.message = systemMessage.getMessageText();
        this.status = systemMessage.getStatusCode();
        this.data = data;
    }
}