package com.example.covault.exceptions;

import com.example.covault.entities.ZZZSystemMessages;
import com.example.covault.enums.SystemMessageType;
import com.example.covault.utils.ZZZCacheMessagesUtility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class APIException extends RuntimeException {
    private final SystemMessageType type;
    private final Object metadata;
    private final StackTraceElement[] originStackTrace;

    @Setter
    @JsonIgnore
    private static ZZZCacheMessagesUtility cacheMessagesUtility;

    public APIException(SystemMessageType type, Object metadata) {
        ZZZSystemMessages systemMessage = cacheMessagesUtility.getSystemMessageByKey(type);

        super(systemMessage == null ? type.name() : systemMessage.getMessageText());
        this.type = type;
        this.metadata = metadata;
        this.originStackTrace = Thread.currentThread().getStackTrace(); // capture at creation time
    }

    public APIException(String errorMessage, Object metadata) {
        ZZZSystemMessages systemMessage = cacheMessagesUtility.getSystemKeyByMessage(errorMessage);

        super(errorMessage);

        this.type = systemMessage == null ? SystemMessageType.valueOf(errorMessage) : SystemMessageType.valueOf(systemMessage.getMessageKey());
        this.metadata = metadata;
        this.originStackTrace = Thread.currentThread().getStackTrace(); // capture at creation time
    }
}
