package com.xii.pillar.repository.workflow;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.xii.pillar.domain.constant.NodeType;
import com.xii.pillar.domain.workflow.PNode;
import com.xii.pillar.repository.BasicRepo;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NodeRepo extends BasicRepo {

    public PNode findNodeByType(String flowId, NodeType nodeType) {
        return findOne(Criteria.where("flowId").is(flowId).and("nodeType").is(nodeType), PNode.class);
    }

    public List<PNode> getNextNodeList(List<String> ids) {
        return getObjects(Criteria.where("preNodeIds").in(ids), PNode.class);
    }

    public boolean updateNode(String id, List<String> preNodeIds) {
        UpdateResult updateResult = updateFirst(Criteria.where("_id").is(id),
                Update.update("preNodeIds", preNodeIds).set("updateAt", System.currentTimeMillis()), PNode.class);
        return updateResult.getModifiedCount() > 0;
    }

    public boolean deleteNode(String id) {
        DeleteResult result = deleteById(id, PNode.class);
        return result.getDeletedCount() > 0;
    }
}
