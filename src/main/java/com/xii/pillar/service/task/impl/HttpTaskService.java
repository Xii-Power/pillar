package com.xii.pillar.service.task.impl;

import com.xii.pillar.domain.snapshot.PTaskSnapshot;
import com.xii.pillar.schema.PException;
import com.xii.pillar.service.task.PTaskService;
import org.springframework.stereotype.Service;

@Service("HttpTask")
public class HttpTaskService implements PTaskService {


    @Override
    public boolean prepare(PTaskSnapshot taskSnapshot, String sessionId) throws PException {
        return false;
    }

    @Override
    public boolean start(PTaskSnapshot taskSnapshot, String sessionId) throws PException {
        return false;
    }

    @Override
    public boolean end(PTaskSnapshot taskSnapshot, String sessionId) throws PException {
        return false;
    }
}
