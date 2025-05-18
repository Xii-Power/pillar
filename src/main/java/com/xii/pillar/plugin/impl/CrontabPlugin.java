package com.xii.pillar.plugin.impl;

import com.xii.pillar.domain.plugin.CrontabTask;
import com.xii.pillar.domain.snapshot.ExecutionPath;
import com.xii.pillar.domain.snapshot.PNodeSnapshot;
import com.xii.pillar.plugin.ExploratoryPlugin;
import com.xii.pillar.repository.plugin.CrontabTaskRepo;
import com.xii.pillar.schema.PContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.xii.pillar.domain.constant.GlobalConstant.TIME_DELAY;

@Service("CrontabPlugin")
public class CrontabPlugin implements ExploratoryPlugin {
    @Autowired
    private CrontabTaskRepo crontabTaskRepo;

    @Override
    public List<String> explore(ExecutionPath path, PContext context, PNodeSnapshot nodeSnapshot) {
        HashMap<String, Object> sessionMap = context.getSessionMap();
        if (!sessionMap.containsKey("crontabId")) {
            return Collections.emptyList();
        }

        long currentTime = System.currentTimeMillis();
        CrontabTask crontabTask = crontabTaskRepo.getById(sessionMap.get("crontabId"), CrontabTask.class);
        if (crontabTask == null
                || crontabTask.getExecAt() > (currentTime + TIME_DELAY))
            return Collections.emptyList();

        // (currentTime-TIME_DELAY) < ExecAt < (currentTime+TIME_DELAY)
        return path.getTaskIds();
    }
}
