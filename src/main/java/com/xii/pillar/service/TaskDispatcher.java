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
import com.xii.pillar.schema.PContext;
import com.xii.pillar.schema.PException;
import com.xii.pillar.service.config.PillarApplicationContextHolder;
import com.xii.pillar.service.config.SessionContextService;
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

import static com.xii.pillar.domain.constant.ErrorOption.RETRY;
import static com.xii.pillar.domain.constant.GlobalConstant.*;
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
     * Scan IN_PROGRESS path
     */
    public void scanPath() {
        List<ExecutionPath> paths = executionPathRepo.scanByState(BaseState.IN_PROGRESS);
        for (ExecutionPath path : paths) {
            GlobalThreadPool.execute(() -> {
                try {
                    ExecutionPath executionPath = dispatchExecutionPath(path.getId());
                    if (executionPath != null) executionPathRepo.toIdleMode(executionPath.getId());
                } catch (Exception e) {
                    log.error("# DISPATCH_PATH_ERROR. id:{}, nodeId:{}", path.getId(), path.getNodeId(), e);
                }
            });
            log.info("# SCAN_PATH. id:{}", path.getId());
        }
    }

    /**
     * Check execution path for node state
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
     * Dispatch tasks of the ExecutionPath
     *
     * @param pathId
     * @return
     * @throws Exception
     */
    public ExecutionPath dispatchExecutionPath(String pathId) throws Exception {
        // select path
        ExecutionPath currentPath = executionPathRepo.selectOne(pathId, BaseState.IN_PROGRESS);
        if (currentPath == null) throw new PException(NODE_ERROR_NULL, "No path in progress");

        Map<String, Object> conditionParams = new HashMap<>();
        BaseState state = checkPathState(currentPath.getId(), conditionParams);

        if (BaseState.PENDING == state) {
            executePath(nodeSnapshotRepo.getById(currentPath.getNodeSnapshotId(), PNodeSnapshot.class), currentPath);
            log.info("# PATH_DISPATCHED. id:{}", currentPath.getId());
            return currentPath;
        }

        if(BaseState.IN_PROGRESS == state) {
            log.info("# PATH_IN_PROGRESS. id:{}", currentPath.getId());
            return currentPath;
        }

        if (BaseState.FINISHED == state && conditionParams.isEmpty()) {
            log.info("# PATH_FINISHED. id:{}", currentPath.getId());
            return executionPathRepo.updateState(currentPath.getId(), BaseState.FINISHED);
        }

        if (BaseState.CANCEL == state && conditionParams.isEmpty()) {
            log.info("# PATH_CANCELED. id:{}", currentPath.getId());
            return executionPathRepo.updateState(currentPath.getId(), BaseState.CANCEL);
        }

        log.info("# PATH_RE_PLAN. id:{}, conditionParams:{}", pathId, conditionParams);
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
            return currentPath;
        }

        // RePlan and create new path for node
        executionPathRepo.updateState(currentPath.getId(), BaseState.CANCEL);
        executionPathRepo.save(newPath.setScanMode(SCAN_MODE_SELECTED));
        executePath(nodeSnapshotRepo.getById(currentPath.getNodeSnapshotId(), PNodeSnapshot.class), newPath);
        return newPath;
    }

    public void executePath(PNodeSnapshot pNodeSnapshot, ExecutionPath executionPath) {
        List<PTaskSnapshot> taskSnapshots = taskSnapshotRepo.getByPathId(executionPath.getId());
        if (!isEmpty(taskSnapshots)) {
            log.info("# EXECUTE_TASK_EXISTS. executionPathId:{}", executionPath.getId());
            return;
        }

        List<String> taskIds = new ArrayList<>();
        switch (executionPath.getPathType()) {
            case general:
            case predict:
                taskIds = executionPath.getTaskIds();
                break;
            case explore:
                // generate task
                ExploratoryPlugin plugin = (ExploratoryPlugin) PillarApplicationContextHolder.getBean(executionPath.getExploratoryPluginName());
                taskIds = plugin.explore(executionPath, sessionContextService.getById(pNodeSnapshot.getSessionId()), pNodeSnapshot);
                break;
        }
        List<PTask> tasks = isEmpty(taskIds) ? null :taskRepo.getByIds(taskIds);
        if(isEmpty(tasks)) {
            log.info("# NO_TASK. executionPathId:{}", executionPath.getId());
            return;
        }

        log.info("# CREATE_TASKS. {}", tasks.size());
        taskSnapshots = new ArrayList<>();

        long createAt = System.currentTimeMillis();
        for (String taskId : taskIds) {
            taskSnapshots.add(PTaskSnapshot.transfer(
                    tasks.stream().filter(t -> taskId.equals(t.getId())).findFirst().get(),
                    executionPath.getId(), createAt));
            createAt = createAt + 1000;
        }
        taskSnapshotRepo.insertAll(taskSnapshots);

        // parallel
        if (executionPath.getExecutionMode() == ExecutionMode.parallel) {
            GlobalThreadPool.batch(taskSnapshots, this::triggerTask, pNodeSnapshot.getSessionId());
            return;
        }

        // serial
        for (PTaskSnapshot taskSnapshot : taskSnapshots) {
            boolean isOK = triggerTask(taskSnapshot, pNodeSnapshot.getSessionId());
            if (!isOK && ErrorOption.isContinue(taskSnapshot.getErrorOption())) {
                log.info("# TASK_ERROR_IGNORE. {}, id:{}", taskSnapshot.getDisplayName(), taskSnapshot.getId());
                continue;
            }


            if (!isOK || !taskSnapshot.getTaskType().isSync()){
                break;
            }
        }
    }


    private BaseState checkPathState(String executionPathId, Map<String, Object> conditionParams) throws Exception {
        BaseState state = BaseState.FINISHED;
        List<PTaskSnapshot> taskSnapshots = taskSnapshotRepo.getByPathId(executionPathId);
        if (isEmpty(taskSnapshots)) return BaseState.PENDING;

        for (PTaskSnapshot taskSnapshot : taskSnapshots) {
            // FAIL
            if (taskSnapshot.getState() == BaseState.FAIL) {
                if (taskSnapshot.getErrorOption() == ErrorOption.BREAK) {
                    state = BaseState.CANCEL;
                    break;
                }

                // prepare conditions for RE_PLAN TASK
                if (ErrorOption.isRePlan(taskSnapshot.getErrorOption())) {
                    HashMap matcherCase = isEmpty(taskSnapshot.getMessage())
                            ? new HashMap<>() : JsonUtil.read(taskSnapshot.getMessage(), HashMap.class);
                    matcherCase.put("returnCode", taskSnapshot.getReturnCode());
                    conditionParams.put(PRE_TASK_CONDITION + taskSnapshot.getTaskId(), matcherCase);
                    state = taskSnapshot.getErrorOption() == ErrorOption.RE_PLAN_NOW ?
                            BaseState.CANCEL : BaseState.IN_PROGRESS;
                }
                continue;
            }

            if (taskSnapshot.getState() != BaseState.IN_PROGRESS) continue;

            // check IN_PROGRESS task expired time
            if (taskSnapshot.getExpireAt() != 0l && taskSnapshot.getExpireAt() > System.currentTimeMillis()) {
                taskSnapshotRepo.updateState(taskSnapshot.getId(), BaseState.IN_PROGRESS, BaseState.FAIL);
                continue;
            }

            state = BaseState.IN_PROGRESS;
        }

        return state;
    }


    private boolean triggerTask(PTaskSnapshot taskSnapshot, String sessionId) {
        PTaskService taskService = (PTaskService) PillarApplicationContextHolder.getBean(taskSnapshot.getName());
        boolean isOK = false;
        try {
            // prepare
            isOK = taskService.prepare(taskSnapshot, sessionId);

            // start
            if(isOK) {
                taskSnapshotRepo.updateState(taskSnapshot.getId(), BaseState.PENDING, BaseState.IN_PROGRESS);
                isOK = taskService.start(taskSnapshot, sessionId);
                while (!isOK && RETRY.equals(taskSnapshot.getErrorOption()) && taskSnapshot.getRemainNum() > 0) {
                    taskSnapshot = taskSnapshot.setRemainNum(taskSnapshot.getRemainNum() - 1);
                    isOK = taskService.start(taskSnapshot, sessionId);
                }

                if (!isOK) taskSnapshotRepo.rePlanSnapshot(taskSnapshot.setState(BaseState.FAIL));
            }

            // end
            if (isOK) {
                PContext context = sessionContextService.getById(sessionId);
                HashMap sessionMap = context.getSessionMap();
                sessionMap.put(taskSnapshot.getName(), taskSnapshot.getMessage());
                sessionContextService.setById(sessionId, context.setSessionMap(sessionMap));
                isOK = taskService.end(taskSnapshot, sessionId);
            }

            if (isOK) taskSnapshotRepo.endSnapshot(taskSnapshot);

        } catch (PException e) {
            log.error("# TRIGGER_TASK_ERROR. sessionId:{}, taskSnapshot:{}", sessionId, taskSnapshot, e);
        }

        return isOK;
    }

}
