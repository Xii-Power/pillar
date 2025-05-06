package com.xii.pillar.service;

import com.googlecode.aviator.AviatorEvaluator;
import com.xii.pillar.domain.constant.BaseState;
import com.xii.pillar.domain.constant.ErrorOption;
import com.xii.pillar.domain.constant.ExecutionMode;
import com.xii.pillar.domain.constant.NodeType;
import com.xii.pillar.domain.snapshot.ExecutionPath;
import com.xii.pillar.domain.snapshot.PNodeSnapshot;
import com.xii.pillar.domain.snapshot.PTaskSnapshot;
import com.xii.pillar.domain.workflow.PTask;
import com.xii.pillar.domain.workflow.PredictionPath;
import com.xii.pillar.plugin.ExploratoryPlugin;
import com.xii.pillar.repository.snapshot.ExecutionPathRepo;
import com.xii.pillar.repository.snapshot.NodeSnapshotRepo;
import com.xii.pillar.repository.snapshot.TaskSnapshotRepo;
import com.xii.pillar.repository.workflow.PredictionPathRepo;
import com.xii.pillar.repository.workflow.TaskRepo;
import com.xii.pillar.schema.PException;
import com.xii.pillar.service.task.PTaskService;
import com.xii.pillar.utils.GlobalThreadPool;
import com.xii.pillar.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.util.ObjectUtils.isEmpty;

@Slf4j
@Service
public class TaskDispatcher {
    @Autowired
    private SessionContextService sessionContextService;
    @Autowired
    private NodeSnapshotRepo nodeSnapshotRepo;
    @Autowired
    private PredictionPathRepo predictionPathRepo;
    @Autowired
    private ExecutionPathRepo executionPathRepo;
    @Autowired
    private TaskSnapshotRepo taskSnapshotRepo;
    @Autowired
    private TaskRepo taskRepo;


    /**
     * check path for the nodeSnapshot state
     * @param nodeSnapshot
     * @return
     */
    public BaseState checkPathState(PNodeSnapshot nodeSnapshot) {
        if (nodeSnapshot.getNodeType() == NodeType.start || nodeSnapshot.getNodeType() == NodeType.end) {
            return BaseState.FINISHED;
        }

        if (executionPathRepo.countByState(nodeSnapshot.getId(), BaseState.IN_PROGRESS) > 0) {
            return BaseState.IN_PROGRESS;
        }

        if (executionPathRepo.countByState(nodeSnapshot.getId(), BaseState.FINISHED) > 0) {
            return BaseState.FINISHED;
        }

        return BaseState.FAIL;
    }

    /**
     * scan and dispatch
     * @return
     */
    public boolean dispatchNodePath() throws Exception {
        // select path
        ExecutionPath currentPath = executionPathRepo.selectOne(BaseState.IN_PROGRESS);
        if (currentPath == null) return false;

        Map<String, Object> conditionParams = new HashMap<>();
        boolean isFinish = checkAndUpdatePath(currentPath.getId(), conditionParams);

        if(!isFinish) {
            log.info("# PATH_EXECUTING. pathId:{}", currentPath.getId());
            return true;
        }

        if (conditionParams.isEmpty()) {
            executionPathRepo.updateState(currentPath.getId(), BaseState.FINISHED);
            return true;
        }

        // process task fail
        List<PredictionPath> paths = predictionPathRepo.getByNodeId(currentPath.getNodeId());
        ExecutionPath newPath = null;
        for (PredictionPath predictionPath : paths) {
            if (predictionPath.getPathType() == PredictionPath.PathType.general
                    || isEmpty(predictionPath.getCondition())) continue;

            // just select first
            if ((Boolean)AviatorEvaluator.execute(predictionPath.getCondition(), conditionParams)) {
                newPath = ExecutionPath.transfer(predictionPath, currentPath.getNodeSnapshotId());
                break;
            }
        }

        if(newPath == null){
            executionPathRepo.updateState(currentPath.getId(), BaseState.CANCEL);
            log.info("# EXECUTE_ALL_PATH_FAIL. nodeId: {}", currentPath.getNodeId());
            return true;
        }

        // RePlan and create new path for node
        executionPathRepo.updateState(currentPath.getId(), BaseState.CANCEL);
        executionPathRepo.save(newPath);
        executePath(nodeSnapshotRepo.getById(currentPath.getNodeSnapshotId(), PNodeSnapshot.class), newPath);
        return true;
    }

    private boolean checkAndUpdatePath(String executionPathId, Map<String, Object> conditionParams) throws Exception {
        boolean isFinish = true;
        List<PTaskSnapshot> taskSnapshots = taskSnapshotRepo.getByPathId(executionPathId);
        for (PTaskSnapshot taskSnapshot : taskSnapshots) {
            // FAIL
            if (taskSnapshot.getState() == BaseState.FAIL) {
                if (taskSnapshot.getErrorOption() == ErrorOption.BREAK) {
                    executionPathRepo.updateState(executionPathId, BaseState.CANCEL);
                    break;
                }

                if (taskSnapshot.getErrorOption() == ErrorOption.RE_PLAN) {
                    HashMap matcherCase = JsonUtil.read(taskSnapshot.getMessage(), HashMap.class);
                    matcherCase.put("returnCode", taskSnapshot.getReturnCode());
                    conditionParams.put(taskSnapshot.getTaskId(), matcherCase);
                }
                continue;
            }

            if (taskSnapshot.getState() != BaseState.IN_PROGRESS) continue;
            // IN_PROGRESS
            if (!isEmpty(taskSnapshot.getExpireAt()) && taskSnapshot.getExpireAt() > System.currentTimeMillis()) {
                taskSnapshotRepo.updateState(taskSnapshot.getId(), BaseState.FAIL);
                continue;
            }

            isFinish = false;
        }

        return isFinish;
    }

    public void executePath(PNodeSnapshot pNodeSnapshot, ExecutionPath executionPath) {
        List<PTaskSnapshot> taskSnapshots = taskSnapshotRepo.getByPathId(executionPath.getId());
        if (!isEmpty(taskSnapshots)) {
            log.info("# EXECUTE_TASK_EXISTS. executionPathId:{}", executionPath.getId());
            return;
        }

        List<PTask> tasks = new ArrayList<>();
        switch (executionPath.getPathType()) {
            case general:
            case predict:
                tasks = taskRepo.getByIds(executionPath.getTaskIds());
                break;
            case explore:
                // generate task
                ExploratoryPlugin plugin = (ExploratoryPlugin) PillarApplicationContextHolder.getBean(executionPath.getExploratoryPluginName());
                List<String> taskIds = plugin.explore(sessionContextService.getById(pNodeSnapshot.getSessionId()), pNodeSnapshot, null);
                tasks = isEmpty(taskIds) ? tasks : taskRepo.getByIds(taskIds);
                break;
        }
        if(isEmpty(tasks)) {
            log.info("# NO_TASK. executionPathId:{}", executionPath.getId());
            return;
        }

        taskSnapshots = tasks.stream().map(t -> PTaskSnapshot.transfer(t, executionPath.getId())).collect(Collectors.toList());
        taskSnapshotRepo.insertAll(taskSnapshots);

        // parallel
        if (executionPath.getExecutionMode() == ExecutionMode.parallel) {
            GlobalThreadPool.batch(taskSnapshots, this::triggerTask, pNodeSnapshot.getSessionId());
            return;
        }

        // serial
        for (PTaskSnapshot taskSnapshot : taskSnapshots) {
            boolean isOK = triggerTask(taskSnapshot, pNodeSnapshot.getSessionId());
            if (!isOK || !taskSnapshot.getTaskType().isSync()){
                break;
            }
        }
    }

    private boolean triggerTask(PTaskSnapshot taskSnapshot, String sessionId) {
        PTaskService taskService = (PTaskService) PillarApplicationContextHolder.getBean(taskSnapshot.getName());
        boolean isOK = false;
        try {
            isOK = taskService.prepare(taskSnapshot, sessionId);
            if(isOK)
                isOK = taskService.start(taskSnapshot, sessionId);

            if (isOK && taskSnapshot.getTaskType().isSync())
                isOK = taskService.end(taskSnapshot, sessionId);

        } catch (PException e) {
            log.error("# TRIGGER_TASK_ERROR. sessionId:{}, taskSnapshot:{}", sessionId, taskSnapshot, e);
        }
        return isOK;
    }

}
