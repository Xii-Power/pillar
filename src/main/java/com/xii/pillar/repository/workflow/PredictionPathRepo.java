package com.xii.pillar.repository.workflow;

import com.xii.pillar.domain.workflow.PredictionPath;
import com.xii.pillar.repository.BasicRepo;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.util.ObjectUtils.isEmpty;

@Component
public class PredictionPathRepo extends BasicRepo {


    public PredictionPath getByType(String nodeId) {
        return findOne(Criteria.where("nodeId").is(nodeId),
                Sort.by(Sort.Direction.ASC, "priority"), PredictionPath.class);
    }

    public List<PredictionPath> getByNodeId(String nodeId) {
        return getObjects(Criteria.where("nodeId").is(nodeId),
                Sort.by(Sort.Direction.ASC, "priority"), PredictionPath.class);
    }

    public boolean updatePath(PredictionPath path) {
        Update update = Update.update("updateAt", System.currentTimeMillis());
        if (!isEmpty(path.getPriority())) update.set("priority", path.getPriority());
        if (!isEmpty(path.getCondition())) update.set("condition", path.getCondition());
        if (!isEmpty(path.getTaskIds())) update.set("taskIds", path.getTaskIds());

        return updateFirst(Criteria.where("_id").is(path.getId()), update, PredictionPath.class)
                .getModifiedCount() > 0;
    }
}
