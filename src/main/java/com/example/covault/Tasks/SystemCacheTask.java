package com.example.covault.Tasks;

import com.example.covault.configs.SystemCache;
import com.example.covault.entities.ZZZActivityMessages;
import com.example.covault.entities.ZZZSystemMessages;
import com.example.covault.repositories.ZZZActivityMessagesRepository;
import com.example.covault.repositories.ZZZSystemMessagesRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class SystemCacheTask {

    private final ZZZSystemMessagesRepository systemMessagesRepository;
    private final ZZZActivityMessagesRepository activityMessagesRepository;
    private final SystemCache systemCache;

    @PostConstruct
    public void setup() {
        // Get all the contents of SystemMessages
        List<ZZZSystemMessages> systemMessagesList = systemMessagesRepository.findAll();
        List<ZZZActivityMessages> activityMessagesList = activityMessagesRepository.findAll();

        // Save to cache
        systemCache.setSystemMessages(systemMessagesList);
        systemCache.setActivityMessages(activityMessagesList);

        // Log how many messages were cached
        log.info("SystemMessagesTask: Cached {} system messages", systemMessagesList.size());
        log.info("ActivityMessagesTask: Cached {} activity messages", activityMessagesList.size());
    }
}