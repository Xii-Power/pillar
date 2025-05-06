package com.xii.pillar.repository.snapshot;

import com.xii.pillar.domain.constant.BaseState;
import com.xii.pillar.domain.snapshot.ExecutionPath;
import com.xii.pillar.domain.snapshot.PFlowSnapshot;
import com.xii.pillar.domain.snapshot.PTaskSnapshot;
import com.xii.pillar.repository.BasicRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import static com.xii.pillar.domain.constant.GlobalConstant.SCAN_MODE_IDLE;
import static com.xii.pillar.domain.constant.GlobalConstant.SCAN_MODE_SELECTED;

@Slf4j
@Component
public class ExecutionPathRepo extends BasicRepo {

    public long countByState(String nodeSnapshotId, BaseState state) {
        return countObjects(Criteria
                .where("nodeSnapshotId").is(nodeSnapshotId).and("state").is(state), ExecutionPath.class);
    }

    public boolean updateState(String id, BaseState state) {
        ExecutionPath executionPath = findAndModify(
                Criteria.where("_id").is(id).and("state").is(BaseState.IN_PROGRESS),
                Update.update("state", state).set("updateAt", System.currentTimeMillis()), ExecutionPath.class);
        return executionPath.getState() == state;
    }


    public ExecutionPath selectOne(BaseState selectedState) {
        ExecutionPath executionPath = findOne(Criteria.where("state").is(selectedState)
                .and("scanMode").is(SCAN_MODE_IDLE), ExecutionPath.class);
        if (executionPath == null) return null;

        long start = System.currentTimeMillis();
        Criteria lockCriteria = Criteria.where("_id").is(executionPath.getId())
                .and("state").is(selectedState)
                .and("scanMode").is(SCAN_MODE_IDLE);

        Update update = Update.update("scanMode", SCAN_MODE_SELECTED);
        update.set("updateTime", System.currentTimeMillis());

        executionPath = findAndModify(lockCriteria, update, ExecutionPath.class);
        if (executionPath == null) return null;

        log.info("select and lock. cost:{}", System.currentTimeMillis() - start);
        return SCAN_MODE_SELECTED.equals(executionPath.getScanMode()) ? executionPath : null;
    }
}
