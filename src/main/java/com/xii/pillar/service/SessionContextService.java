package com.xii.pillar.service;

import com.xii.pillar.schema.PContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SessionContextService {

    public PContext getById(String sessionId) {
        // TODO get from cache

        return null;
    }
}
