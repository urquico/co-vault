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
    private final String email;
    private final Object metadata;
    private final ZZZCacheMessagesUtility zzzCacheMessagesUtility;
    private final StackTraceElement[] originStackTrace;

    @Setter
    @JsonIgnore
    private static ZZZCacheMessagesUtility cacheMessagesUtility;

    public APIException(SystemMessageType type, String email, Object metadata) {
        ZZZSystemMessages systemMessage = cacheMessagesUtility.getSystemMessageByKey(type);

        super(systemMessage.getMessageText());
        this.type = type;
        this.email = email;
        this.metadata = metadata;
        this.zzzCacheMessagesUtility = cacheMessagesUtility;
        this.originStackTrace = Thread.currentThread().getStackTrace(); // capture at creation time
    }
}
