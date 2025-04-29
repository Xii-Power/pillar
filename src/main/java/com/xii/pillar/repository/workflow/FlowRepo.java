package com.xii.pillar.repository.workflow;

import com.mongodb.client.result.UpdateResult;
import com.xii.pillar.domain.constant.BaseState;
import com.xii.pillar.domain.workflow.PFlow;
import com.xii.pillar.repository.BasicRepo;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

@Component
public class FlowRepo extends BasicRepo {

    public boolean updateState(String id, BaseState state) {
        UpdateResult updateResult = mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(id)),
                Update.update("status", state).set("updateAt", System.currentTimeMillis()), PFlow.class);
        return updateResult.getModifiedCount() > 0;
    }
}
