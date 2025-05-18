package com.xii.pillar.service.task.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.googlecode.aviator.AviatorEvaluator;
import com.xii.pillar.connect.ConnectorType;
import com.xii.pillar.connect.SessionHolder;
import com.xii.pillar.domain.snapshot.PTaskSnapshot;
import com.xii.pillar.schema.PContext;
import com.xii.pillar.schema.PException;
import com.xii.pillar.service.config.SessionContextService;
import com.xii.pillar.service.task.PTaskService;
import com.xii.pillar.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Slf4j
@Service("TextInteraction")
public class TextInteractionService implements PTaskService {
    @Autowired
    private SessionContextService sessionContextService;

    @Override
    public boolean prepare(PTaskSnapshot taskSnapshot, String sessionId) throws PException {
        PContext context = sessionContextService.getById(sessionId);
        HashMap<String, String> params = taskSnapshot.getParams();
        taskSnapshot.getContextParser().forEach((key, parser) -> {
            params.put(key, (String) AviatorEvaluator.execute(parser, context.getSessionMap()));
        });

        taskSnapshot.setParams(params);
        return true;
    }

    @Override
    public boolean start(PTaskSnapshot taskSnapshot, String sessionId) throws PException {
        try {
            PContext context = sessionContextService.getById(sessionId);
            ObjectNode message = JsonUtil.toObjectNode(taskSnapshot.getParams());
            SessionHolder.sendTextMessage(ConnectorType.INTERACTION, context.getDeviceId(), message);
            return true;
        } catch (Exception e) {
            log.error("# TEXT_INTERACTION_FAIL. {}", taskSnapshot.getId(), e);
        }
        return false;
    }

    @Override
    public boolean end(PTaskSnapshot taskSnapshot, String sessionId) throws PException {
        return true;
    }
}
