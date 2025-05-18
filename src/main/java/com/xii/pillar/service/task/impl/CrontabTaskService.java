package com.xii.pillar.service.task.impl;

import com.xii.pillar.domain.constant.BaseState;
import com.xii.pillar.domain.constant.ErrorOption;
import com.xii.pillar.domain.plugin.CrontabTask;
import com.xii.pillar.domain.snapshot.PTaskSnapshot;
import com.xii.pillar.repository.plugin.CrontabTaskRepo;
import com.xii.pillar.schema.PContext;
import com.xii.pillar.schema.PException;
import com.xii.pillar.service.config.SessionContextService;
import com.xii.pillar.service.task.PTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static com.xii.pillar.domain.constant.GlobalConstant.TIME_DELAY;

@Slf4j
@Service("CrontabTask")
public class CrontabTaskService implements PTaskService {
    @Autowired
    private SessionContextService sessionContextService;
    @Autowired
    private CrontabTaskRepo crontabTaskRepo;

    @Override
    public boolean prepare(PTaskSnapshot taskSnapshot, String sessionId) throws PException {
        PContext context = sessionContextService.getById(sessionId);
        HashMap<String, Object> sessionMap = context.getSessionMap();
        if (!sessionMap.containsKey("crontabId")) {
            CrontabTask task = new CrontabTask(
                    sessionMap.containsKey("perNum") ? (Integer) sessionMap.get("perNum") : null,
                    sessionMap.containsKey("unit") ? sessionMap.get("unit").toString() : null,
                    sessionMap.containsKey("execAt") ? Long.valueOf((String) sessionMap.get("execAt")) : null,
                    sessionMap.containsKey("execType")
                            ? CrontabTask.ExecType.valueOf(sessionMap.get("execType").toString())
                            : CrontabTask.ExecType.ONE_TIME);

            if (task.getExecType() == CrontabTask.ExecType.REPEAT) {
                task.setExecAt(System.currentTimeMillis() + parse(task.getUnit()).toMillis(task.getPerNum()));
            }
            crontabTaskRepo.save(task);
            sessionMap.put("crontabId", task.getId());
        }

        log.info("# CRONTAB_TASK. sessionMap:{}", sessionMap);
        return true;
    }

    @Override
    public boolean start(PTaskSnapshot taskSnapshot, String sessionId) throws PException {
        PContext context = sessionContextService.getById(sessionId);
        HashMap<String, Object> sessionMap = context.getSessionMap();
        if (!sessionMap.containsKey("crontabId")) {
            taskSnapshot.setErrorOption(ErrorOption.RE_PLAN_NOW);
            return false;
        }

        CrontabTask task = crontabTaskRepo.getById(sessionMap.get("crontabId"), CrontabTask.class);
        long currentTime = System.currentTimeMillis();
        if (task.getExecAt() > (currentTime + TIME_DELAY)) {
            taskSnapshot.setReturnCode("WAIT").setErrorOption(ErrorOption.RE_PLAN_NOW);
            crontabTaskRepo.updateState(task.getId(), BaseState.IN_PROGRESS);
            return false;
        }

        if (task.getExecAt() < (currentTime - TIME_DELAY)) {
            log.info("# EXEC_TIMEOUT. id:{}, current:{}, execAt:{}", task.getId(), currentTime, task.getExecAt());
            taskSnapshot.setReturnCode("EXPIRED").setErrorOption(ErrorOption.RE_PLAN_NOW);
            crontabTaskRepo.updateState(task.getId(), BaseState.FAIL);
            return false;
        }

        // (currentTime-TIME_DELAY) < ExecAt < (currentTime+TIME_DELAY) 不结束任务，继续下一周期
        if (task.getExecType() == CrontabTask.ExecType.REPEAT) {
            taskSnapshot.setReturnCode("REPEAT").setErrorOption(ErrorOption.RE_PLAN_NEXT);
            crontabTaskRepo.updateState(task.getId(), BaseState.IN_PROGRESS);
            return false;
        }

        taskSnapshot.setState(BaseState.FINISHED).setReturnCode("OK");
        crontabTaskRepo.updateState(task.getId(), BaseState.FINISHED);
        return true;
    }

    @Override
    public boolean end(PTaskSnapshot taskSnapshot, String sessionId) throws PException {
        return true;
    }

    private TimeUnit parse(String unit) {
        switch (unit) {
            case "MIN":
                return TimeUnit.MINUTES;
            case "HOUR":
                return TimeUnit.HOURS;
            case "DAY":
                return TimeUnit.DAYS;
        }
        return TimeUnit.MINUTES;
    }
}
