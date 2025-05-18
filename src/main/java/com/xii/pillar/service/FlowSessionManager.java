package com.xii.pillar.service;

import com.xii.pillar.domain.constant.BaseState;
import com.xii.pillar.domain.constant.GlobalConstant;
import com.xii.pillar.domain.constant.NodeType;
import com.xii.pillar.domain.snapshot.ExecutionPath;
import com.xii.pillar.domain.snapshot.PFlowSnapshot;
import com.xii.pillar.domain.snapshot.PNodeSnapshot;
import com.xii.pillar.domain.workflow.PFlow;
import com.xii.pillar.domain.workflow.PNode;
import com.xii.pillar.domain.workflow.PredictionPath;
import com.xii.pillar.repository.snapshot.FlowSnapshotRepo;
import com.xii.pillar.repository.snapshot.NodeSnapshotRepo;
import com.xii.pillar.repository.workflow.FlowRepo;
import com.xii.pillar.repository.workflow.NodeRepo;
import com.xii.pillar.repository.workflow.PredictionPathRepo;
import com.xii.pillar.service.config.SessionContextService;
import com.xii.pillar.utils.GlobalThreadPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.xii.pillar.domain.constant.GlobalConstant.SCAN_MODE_SELECTED;
import static org.springframework.util.ObjectUtils.isEmpty;

@Slf4j
@Service
public class FlowSessionManager {
    @Autowired
    private FlowRepo flowRepo;
    @Autowired
    private FlowSnapshotRepo flowSnapshotRepo;
    @Autowired
    private NodeRepo nodeRepo;
    @Autowired
    private NodeSnapshotRepo nodeSnapshotRepo;
    @Autowired
    private PredictionPathRepo predictionPathRepo;
    @Autowired
    private SessionContextService sessionContextService;
    @Autowired
    private TaskDispatcher taskDispatcher;

    /**
     * Scan session cache and process flow
     */
    public void scanSession() {
        Set<String> ids = sessionContextService.getSessionIds();
        for (String id : ids) {
            GlobalThreadPool.execute(() -> {
                try {
                    PFlowSnapshot flowSnapshot = processFlow(id);
                    if (flowSnapshot != null)
                        flowSnapshotRepo.updateScanMode(flowSnapshot.getId(), GlobalConstant.SCAN_MODE_IDLE);

                    log.info("# SCAN_FLOW_SESSION_END. {}", id);
                } catch (Exception e) {
                    log.error("# PROCESS_FLOW_ERROR. sessionId:{}", id, e);
                }
            });
            log.info("# SCAN_SESSION. id:{}", id);
        }
    }

    public PFlowSnapshot createSession(String sessionId, String flowId, boolean startNow) {
        PFlow flow = flowRepo.getById(flowId, PFlow.class);
        if (flow == null) return null;

        PNode startNode = nodeRepo.findNodeByType(flowId, NodeType.start);
        if (startNode == null) return null;

        PFlowSnapshot flowSnapshot = PFlowSnapshot.transfer(flow, sessionId);
        PNodeSnapshot nodeSnapshot = PNodeSnapshot.transfer(startNode, new ArrayList<>(), flowSnapshot.getId(), flowSnapshot.getSessionId());
        if (startNow) {
            flowSnapshot = flowSnapshot.setState(BaseState.IN_PROGRESS);
            nodeSnapshot = nodeSnapshot.setState(BaseState.IN_PROGRESS);
        }

        // save and start flow snapshot
        flowSnapshotRepo.insertAll(Arrays.asList(nodeSnapshot, flowSnapshot));
        return flowSnapshot;
    }

    /**
     * 按优先级，依次启动工作流
     * PENDING -> IN_PROGRESS
     * @param sessionId
     * @return
     */
    public void startFlow(String sessionId) {
        long num = flowSnapshotRepo.countByState(sessionId, BaseState.IN_PROGRESS);
        if(num > 0) {
            return ;
        }

        // start new flow
        PFlowSnapshot flowSnapshot = flowSnapshotRepo.selectOne(sessionId, BaseState.PENDING, BaseState.IN_PROGRESS);
        if (flowSnapshot == null) return ;

        executeFlow(flowSnapshot, nodeSnapshotRepo.findByType(flowSnapshot.getId(), NodeType.start));
    }



    /**
     * 处理进行中工作流
     * 1. 当Node存在FAIL状态，工作流状态为FAIL
     * 2. 当END Node处于完成状态，工作流状态为FINISHED
     * 3. 扫描IN_PROGRESS状态Node，并根据ExecutionPath状态判定Node状态
     * 4. 扫描并处理下一个Node
     *
     * @param sessionId
     */
    public PFlowSnapshot processFlow(String sessionId) {
        PFlowSnapshot flowSnapshot = flowSnapshotRepo.selectOne(sessionId, BaseState.IN_PROGRESS, BaseState.IN_PROGRESS);
        if (flowSnapshot == null) return flowSnapshot;
        // process FAIL
        List<PNodeSnapshot> nodeSnapshots = nodeSnapshotRepo.getSnapshotsByState(flowSnapshot.getId(), BaseState.FAIL);
        if(!isEmpty(nodeSnapshots)) {
            log.info("# WORKFLOW_FAIL. nodeSnapshotIds:{}", nodeSnapshots);
            flowSnapshotRepo.updateState(flowSnapshot.getId(), BaseState.FAIL);
            sessionContextService.removeById(sessionId);
            return flowSnapshot;
        }

        // process FINISHED
        nodeSnapshots = nodeSnapshotRepo.getSnapshotsByState(flowSnapshot.getId(), BaseState.IN_PROGRESS);
        if (isEmpty(nodeSnapshots)) {
            PNodeSnapshot nodeSnapshot = nodeSnapshotRepo.findByType(flowSnapshot.getId(), NodeType.end);
            if(nodeSnapshot != null && nodeSnapshot.getState() == BaseState.FINISHED) {
                log.info("# FINISH_FLOW. flowId:{}, snapshotId:{}", flowSnapshot.getFlowId(), flowSnapshot.getId());
                flowSnapshotRepo.updateState(flowSnapshot.getId(), BaseState.FINISHED);
                // TODO backend message
                sessionContextService.removeById(sessionId);
                return flowSnapshot;
            }

            flowSnapshot = flowSnapshotRepo.updateScanMode(flowSnapshot.getId(), GlobalConstant.SCAN_MODE_SELECTED);
            log.info("# PROCESS_FLOW_EMPTY. {} flowSnapshotId:{}", flowSnapshot.getId(), flowSnapshot.getScanMode());
            return flowSnapshot;
        }

        // process IN_PROGRESS
        List<String> failIds = new ArrayList<>();
        List<PNodeSnapshot> finishNodeSnapshots = new ArrayList<>();
        BaseState state;
        for (PNodeSnapshot snapshot : nodeSnapshots) {
            state = taskDispatcher.checkPathState(snapshot);
            if (state != snapshot.getState()) {
                nodeSnapshotRepo.updateState(snapshot.getId(), state);
                log.info("# UPDATE_NODE_STATE. id:{}, state:{}", snapshot.getId(), state);
            }

            if (state == BaseState.FAIL) {
                failIds.add(snapshot.getNodeId());
            } else if (state == BaseState.FINISHED) {
                finishNodeSnapshots.add(snapshot);
            }
        }

        // stop flow and change state
        if(!isEmpty(failIds)) {
            log.info("# WORKFLOW_PROCESS_FAIL. nodeIds:{}", failIds);
            sessionContextService.removeById(sessionId);
            flowSnapshotRepo.updateState(flowSnapshot.getId(), BaseState.FAIL);
            return flowSnapshot;
        }

        // finish current and create next node
        processNextNode(flowSnapshot, finishNodeSnapshots);
        return flowSnapshot;
    }

    private void processNextNode(PFlowSnapshot flowSnapshot, List<PNodeSnapshot> finishNodeSnapshots) {
        if (isEmpty(finishNodeSnapshots)) return;

        List<PNode> nodes;
        for (PNodeSnapshot nodeSnapshot : finishNodeSnapshots) {
            List<String> preFinishedIds = new ArrayList<>();
            if (nodeSnapshot.getNodeType() != NodeType.start) {
                List<PNodeSnapshot> preNodeSnapshots = nodeSnapshotRepo.getPreNodeSnapshots(flowSnapshot.getId(), nodeSnapshot.getPreNodeSnapshotIds());
                preFinishedIds = preNodeSnapshots.stream()
                        .filter(n -> n.getState() == BaseState.FINISHED)
                        .map(PNodeSnapshot::getId)
                        .collect(Collectors.toList());
                if (preFinishedIds.size() != preNodeSnapshots.size()) {
                    log.info("# NODE_DEPEND_MORE. finished {}", preFinishedIds);
                    continue;
                }
            }

            nodes = nodeRepo.getNextNodeList(Arrays.asList(nodeSnapshot.getNodeId()));
            for (PNode node : nodes) {
                PNodeSnapshot newNodeSnapshot = PNodeSnapshot.transfer(node, preFinishedIds, flowSnapshot.getId(), flowSnapshot.getSessionId());
                nodeSnapshotRepo.save(newNodeSnapshot.setState(BaseState.IN_PROGRESS));
                executeFlow(flowSnapshot, newNodeSnapshot);
            }
        }

    }

    public void executeFlow(PFlowSnapshot flowSnapshot, PNodeSnapshot nodeSnapshot) {
        if (flowSnapshot == null || nodeSnapshot == null) return ;

        if (nodeSnapshot.getNodeType() == NodeType.end) {
            nodeSnapshotRepo.updateState(nodeSnapshot.getId(), BaseState.FINISHED);
            log.info("# FLOW_END. {} session:{}", flowSnapshot.getName(), flowSnapshot.getSessionId());
            return ;
        }

        if (nodeSnapshot.getNodeType() == NodeType.start) {
            nodeSnapshotRepo.updateState(nodeSnapshot.getId(), BaseState.FINISHED);
            processNextNode(flowSnapshot, Arrays.asList(nodeSnapshot));
            log.info("# FLOW_START. {} session:{}", flowSnapshot.getName(), flowSnapshot.getSessionId());
            return ;
        }

        // create new Path
        PredictionPath path = predictionPathRepo.getByType(nodeSnapshot.getNodeId());
        if (path == null) {
            nodeSnapshotRepo.updateState(nodeSnapshot.getId(), BaseState.FINISHED);
            processNextNode(flowSnapshot, Arrays.asList(nodeSnapshot));
            log.info("# NO_PATH_EXECUTE. {} session:{}", nodeSnapshot.getName(), flowSnapshot.getSessionId());
            return;
        }

        flowSnapshotRepo.save(ExecutionPath.transfer(path, nodeSnapshot.getId())); // TODO don't safe
    }

    public boolean cancelFlow(String sessionId, String flowId) {
        PFlowSnapshot flowSnapshot = flowSnapshotRepo.getBySession(sessionId, flowId);
        if (flowSnapshot == null) return false;

        flowSnapshot = flowSnapshotRepo.updateState(flowSnapshot.getId(), BaseState.CANCEL);
        return flowSnapshot != null;
    }


}
