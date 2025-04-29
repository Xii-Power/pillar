package com.xii.pillar.repository.workflow;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.xii.pillar.domain.workflow.PNode;
import com.xii.pillar.repository.BasicRepo;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NodeRepo extends BasicRepo {

    public boolean updateNode(String id, List<String> preNodeIds) {
        UpdateResult updateResult = mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(id)),
                Update.update("preNodeIds", preNodeIds).set("updateAt", System.currentTimeMillis()), PNode.class);
        return updateResult.getModifiedCount() > 0;
    }

    public boolean deleteNode(String id) {
        DeleteResult result = mongoTemplate.remove(Query.query(Criteria.where("_id").is(id)), PNode.class);
        return result.getDeletedCount() > 0;
    }
}
