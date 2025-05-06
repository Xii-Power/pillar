package com.xii.pillar.repository.snapshot;

import com.xii.pillar.domain.constant.BaseState;
import com.xii.pillar.domain.constant.NodeType;
import com.xii.pillar.domain.snapshot.PNodeSnapshot;
import com.xii.pillar.repository.BasicRepo;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.xii.pillar.domain.constant.BaseState.IN_PROGRESS;

@Component
public class NodeSnapshotRepo extends BasicRepo {

    public PNodeSnapshot findByType(String flowSnapshotId, NodeType nodeType) {
        return findOne(Criteria.where("flowSnapshotId").is(flowSnapshotId).and("nodeType").is(nodeType),
                PNodeSnapshot.class);
    }

    public List<PNodeSnapshot> getSnapshotsByState(String flowSnapshotId, BaseState state) {
        return getObjects(Criteria.where("flowSnapshotId").is(flowSnapshotId).and("state").is(state),
                PNodeSnapshot.class);
    }

    public List<PNodeSnapshot> getPreNodeSnapshots(String flowSnapshotId, List<String> preNodeIds) {
        return getObjects(Criteria.where("flowSnapshotId").is(flowSnapshotId).and("preNodeIds").in(preNodeIds),
                PNodeSnapshot.class);
    }

    public PNodeSnapshot updateState(String id, BaseState state) {
        return findAndModify(Criteria.where("_id").is(id).and("state").is(IN_PROGRESS),
                Update.update("state", state), PNodeSnapshot.class);
    }
}
