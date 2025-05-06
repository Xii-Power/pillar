package com.xii.pillar.repository.workflow;

import com.mongodb.client.result.UpdateResult;
import com.xii.pillar.domain.workflow.PTask;
import com.xii.pillar.repository.BasicRepo;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.List;

@Component
public class TaskRepo extends BasicRepo {

    public List<PTask> getByIds(List<String> ids) {
        return getObjects(Criteria.where("_id").in(ids), PTask.class);
    }

    public boolean updateTask(String id, HashMap<String, String> contextParser, HashMap<String, String> params) {
        Update update = Update.update("updateAt", System.currentTimeMillis());
        if (ObjectUtils.isEmpty(contextParser)) {
            update.set("contextParser", contextParser);
        }

        if (ObjectUtils.isEmpty(params)) {
            update.set("params", params);
        }

        UpdateResult updateResult = updateFirst(Criteria.where("_id").is(id), update, PTask.class);
        return updateResult.getModifiedCount() > 0;
    }
}
