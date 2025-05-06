package com.xii.pillar.repository.workflow;

import com.xii.pillar.domain.workflow.PredictionPath;
import com.xii.pillar.repository.BasicRepo;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PredictionPathRepo extends BasicRepo {


    public PredictionPath getByType(String nodeId, PredictionPath.PathType pathType) {
        return findOne(Criteria.where("nodeId").is(nodeId).and("pathType").is(pathType),
                Sort.by(Sort.Direction.ASC, "priority"), PredictionPath.class);
    }

    public List<PredictionPath> getByNodeId(String nodeId) {
        return getObjects(Criteria.where("nodeId").is(nodeId),
                Sort.by(Sort.Direction.ASC, "priority"), PredictionPath.class);
    }
}
