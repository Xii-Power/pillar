package com.xii.pillar.service.task.impl;

import com.xii.pillar.domain.snapshot.PTaskSnapshot;
import com.xii.pillar.schema.PException;
import com.xii.pillar.service.config.SessionContextService;
import com.xii.pillar.service.task.PTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("MessageTask")
public class MessageTaskService implements PTaskService {
    @Autowired
    private SessionContextService sessionContextService;


    @Override
    public boolean prepare(PTaskSnapshot taskSnapshot, String sessionId) throws PException {
        return true;
    }

    @Override
    public boolean start(PTaskSnapshot taskSnapshot, String sessionId) throws PException {
        return false;
    }

    @Override
    public boolean end(PTaskSnapshot taskSnapshot, String sessionId) throws PException {
        return true;
    }
}
