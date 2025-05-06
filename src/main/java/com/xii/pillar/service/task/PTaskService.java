package com.xii.pillar.service.task;

import com.xii.pillar.domain.snapshot.PTaskSnapshot;
import com.xii.pillar.schema.PException;

public interface PTaskService {

    boolean prepare(PTaskSnapshot taskSnapshot, String sessionId) throws PException;

    boolean start(PTaskSnapshot taskSnapshot, String sessionId) throws PException;

    boolean end(PTaskSnapshot taskSnapshot, String sessionId) throws PException;
}
