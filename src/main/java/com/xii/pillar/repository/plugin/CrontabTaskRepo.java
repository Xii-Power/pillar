package com.xii.pillar.repository.plugin;

import com.mongodb.client.result.UpdateResult;
import com.xii.pillar.domain.constant.BaseState;
import com.xii.pillar.domain.plugin.CrontabTask;
import com.xii.pillar.repository.BasicRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CrontabTaskRepo extends BasicRepo {

    public void updateState(String id, BaseState state) {
        updateFirst(Criteria.where("_id").is(id), Update.update("state", state), CrontabTask.class);
    }
}
