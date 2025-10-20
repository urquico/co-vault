package com.example.covault.utils;

import com.example.covault.configs.SystemCache;
import com.example.covault.entities.ZZZActivityMessages;
import com.example.covault.entities.ZZZSystemMessages;
import com.example.covault.enums.ActivityType;
import com.example.covault.enums.SystemMessageType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ZZZCacheMessagesUtility {

    private final SystemCache systemCache;

    public ZZZSystemMessages getSystemMessageByKey(SystemMessageType key) {
        return systemCache.getSystemMessages()
                .stream()
                .filter(m -> m.getMessageKey().equals(key.toString()))
                .findFirst()
                .orElse(null);
    }

    public ZZZSystemMessages getSystemKeyByMessage(String message) {
        return systemCache.getSystemMessages()
                .stream()
                .filter(m -> m.getMessageText().equalsIgnoreCase(message))
                .findFirst()
                .orElse(null);
    }

    public ZZZActivityMessages getActivityMessageByKey(ActivityType key) {
        return systemCache.getActivityMessages()
                .stream()
                .filter(m -> m.getMessageKey().equals(key.toString()))
                .findFirst()
                .orElse(null);
    }
}