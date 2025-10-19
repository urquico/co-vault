package com.example.covault.configs;

import com.example.covault.dtos.APIResponse;
import com.example.covault.exceptions.APIException;
import com.example.covault.utils.ZZZCacheMessagesUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ZZZCacheMessagesInjector {

    @Autowired
    public ZZZCacheMessagesInjector(ZZZCacheMessagesUtility utility) {
        APIResponse.setCacheMessagesUtility(utility);
        APIException.setCacheMessagesUtility(utility);
        System.out.println("✅ ZZZCacheMessagesUtility injected into APIResponse");
    }
}
