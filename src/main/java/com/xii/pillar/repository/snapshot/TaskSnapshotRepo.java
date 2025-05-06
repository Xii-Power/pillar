package com.xii.pillar.repository.snapshot;

import com.xii.pillar.domain.constant.BaseState;
import com.xii.pillar.domain.snapshot.PTaskSnapshot;
import com.xii.pillar.repository.BasicRepo;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskSnapshotRepo extends BasicRepo {

    public List<PTaskSnapshot> getByPathId(String executionPathId) {
        return getObjects(Criteria.where("executionPathId").is(executionPathId), PTaskSnapshot.class);
    }

    public boolean updateState(String id, BaseState state) {
        PTaskSnapshot taskSnapshot = findAndModify(
                Criteria.where("_id").is(id).and("state").is(BaseState.IN_PROGRESS),
                Update.update("state", state).set("updateAt", System.currentTimeMillis()), PTaskSnapshot.class);
        return taskSnapshot.getState() == state;
    }
}
