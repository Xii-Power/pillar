package com.xii.pillar.repository.snapshot;

import com.xii.pillar.domain.constant.BaseState;
import com.xii.pillar.domain.snapshot.PTaskSnapshot;
import com.xii.pillar.repository.BasicRepo;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskSnapshotRepo extends BasicRepo {

    public List<PTaskSnapshot> getByPathId(String executionPathId) {
        return getObjects(Criteria.where("executionPathId").is(executionPathId),
                Sort.by(Sort.Direction.ASC,"createAt"), PTaskSnapshot.class);
    }

    public boolean updateState(String id, BaseState originState, BaseState state) {
        PTaskSnapshot taskSnapshot = findAndModify(
                Criteria.where("_id").is(id).and("state").is(originState),
                Update.update("state", state)
                        .set(BaseState.IN_PROGRESS == originState ? "endAt" : "startAt", System.currentTimeMillis()),
                PTaskSnapshot.class);
        return taskSnapshot.getState() == state;
    }

    public boolean rePlanSnapshot(PTaskSnapshot snapshot) {
        PTaskSnapshot updatedSnapshot = findAndModify(
                Criteria.where("_id").is(snapshot.getId()).and("state").is(BaseState.IN_PROGRESS),
                Update.update("state", snapshot.getState())
                        .set("errorOption", snapshot.getErrorOption())
                        .set("returnCode", snapshot.getReturnCode())
                        .set("message", snapshot.getMessage())
                        .set("endAt", System.currentTimeMillis()),
                PTaskSnapshot.class);
        return updatedSnapshot.getState() == snapshot.getState();
    }

    public boolean endSnapshot(PTaskSnapshot taskSnapshot) {
        taskSnapshot = findAndModify(
                Criteria.where("_id").is(taskSnapshot.getId()).and("state").is(BaseState.IN_PROGRESS),
                Update.update("state", BaseState.FINISHED)
                        .set("endAt", System.currentTimeMillis())
                        .set("returnCode", taskSnapshot.getReturnCode())
                        .set("message", taskSnapshot.getMessage()), PTaskSnapshot.class);
        return taskSnapshot.getState() == BaseState.FINISHED;
    }
}
