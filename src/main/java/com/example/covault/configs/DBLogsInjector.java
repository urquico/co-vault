package com.example.covault.configs;

import com.example.covault.dtos.APIResponse;
import com.example.covault.utils.DBLogsUtility;
import com.example.covault.utils.ZZZCacheMessagesUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DBLogsInjector {

    @Autowired
    public DBLogsInjector(DBLogsUtility utility) {
        APIResponse.setDbLogsUtility(utility);
        System.out.println("✅ DBLogsUtility injected into APIResponse");
    }
}
