package com.xii.pillar.repository.snapshot;

import com.xii.pillar.domain.constant.BaseState;
import com.xii.pillar.domain.snapshot.PFlowSnapshot;
import com.xii.pillar.repository.BasicRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import static com.xii.pillar.domain.constant.BaseState.IN_PROGRESS;
import static com.xii.pillar.domain.constant.BaseState.PENDING;
import static com.xii.pillar.domain.constant.GlobalConstant.SCAN_MODE_IDLE;
import static com.xii.pillar.domain.constant.GlobalConstant.SCAN_MODE_SELECTED;

@Slf4j
@Component
public class FlowSnapshotRepo extends BasicRepo {

    public long countByState(String sessionId, BaseState state) {
        return countObjects(Criteria.where("state").is(state).and("sessionId").is(sessionId), PFlowSnapshot.class);
    }

    public PFlowSnapshot selectOne(String sessionId, BaseState selectedState, BaseState updatedState) {
        Criteria criteria = Criteria.where("state").is(selectedState)
                .and("scanMode").is(SCAN_MODE_IDLE)
                .and("sessionId").is(sessionId);
        PFlowSnapshot flowSnapshot = findOne(criteria, Sort.by(Sort.Direction.ASC, "priority"), PFlowSnapshot.class);
        if (flowSnapshot == null) return null;

        long start = System.currentTimeMillis();
        Criteria lockCriteria = Criteria.where("_id").is(flowSnapshot.getId())
                .and("state").is(selectedState)
                .and("scanMode").is(SCAN_MODE_IDLE);

        Update update = Update.update("state", updatedState);
        update.set("scanMode", SCAN_MODE_SELECTED);
        update.set("updateTime", System.currentTimeMillis());
        flowSnapshot = findAndModify(lockCriteria, update, PFlowSnapshot.class);
        if (flowSnapshot == null) return null;

        log.info("select and lock. cost:{}", System.currentTimeMillis() - start);
        return flowSnapshot.getState() == updatedState ? flowSnapshot : null;
    }

    public PFlowSnapshot getBySession(String sessionId, String flowId) {
        return findOne(Criteria.where("flowId").is(flowId)
                .and("sessionId").is(sessionId)
                .and("state").is(IN_PROGRESS), PFlowSnapshot.class);
    }

    public PFlowSnapshot updateState(String id, BaseState state) {
        return findAndModify(Criteria.where("_id").is(id).and("state").is(IN_PROGRESS),
                Update.update("state", state), PFlowSnapshot.class);
    }

    public PFlowSnapshot updateScanMode(String id, String scanMode) {
        return findAndModify(Criteria.where("_id").is(id), Update.update("scanMode", scanMode), PFlowSnapshot.class);
    }

}
