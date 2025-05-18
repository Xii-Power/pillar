package com.xii.pillar.service.config;

import com.xii.pillar.schema.PContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class SessionContextService {

    // TODO cache
    private Map<String, PContext> contextMap = new HashMap<>();


    public Set<String> getSessionIds() {
        return contextMap.keySet();
    }

    public void setById(String sessionId, PContext context) {
        contextMap.put(sessionId, context);
    }

    public PContext getById(String sessionId) {
        return contextMap.get(sessionId);
    }

    public PContext removeById(String sessionId) {
        return contextMap.remove(sessionId);
    }
}
