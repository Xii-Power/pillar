package com.xii.pillar.repository.snapshot;

import com.xii.pillar.domain.constant.BaseState;
import com.xii.pillar.domain.snapshot.ExecutionPath;
import com.xii.pillar.repository.BasicRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.xii.pillar.domain.constant.GlobalConstant.SCAN_MODE_IDLE;
import static com.xii.pillar.domain.constant.GlobalConstant.SCAN_MODE_SELECTED;

@Slf4j
@Component
public class ExecutionPathRepo extends BasicRepo {

    public long countByState(String nodeSnapshotId, BaseState state) {
        return countObjects(Criteria
                .where("nodeSnapshotId").is(nodeSnapshotId).and("state").is(state), ExecutionPath.class);
    }

    public ExecutionPath updateState(String id, BaseState state) {
        return findAndModify(
                Criteria.where("_id").is(id).and("state").is(BaseState.IN_PROGRESS),
                Update.update("state", state).set("updateAt", System.currentTimeMillis()), ExecutionPath.class);
    }

    public List<ExecutionPath> scanByState(BaseState selectedState) {
       return getObjects(Criteria.where("state").is(selectedState)
               .and("scanMode").is(SCAN_MODE_IDLE), ExecutionPath.class);
    }

    public ExecutionPath selectOne(String id, BaseState selectedState) {
        long start = System.currentTimeMillis();
        Criteria lockCriteria = Criteria.where("_id").is(id)
                .and("state").is(selectedState)
                .and("scanMode").is(SCAN_MODE_IDLE);

        Update update = Update.update("scanMode", SCAN_MODE_SELECTED);
        update.set("updateTime", System.currentTimeMillis());

        ExecutionPath executionPath = findAndModify(lockCriteria, update, ExecutionPath.class);
        if (executionPath == null) return null;

        log.info("select and lock. cost:{}", System.currentTimeMillis() - start);
        return SCAN_MODE_SELECTED.equals(executionPath.getScanMode()) ? executionPath : null;
    }

    public boolean toIdleMode(String id) {
        ExecutionPath executionPath = findAndModify(
                Criteria.where("_id").is(id).and("scanMode").is(SCAN_MODE_SELECTED),
                Update.update("scanMode", SCAN_MODE_IDLE).set("updateAt", System.currentTimeMillis()), ExecutionPath.class);
        return executionPath != null;
    }
}
