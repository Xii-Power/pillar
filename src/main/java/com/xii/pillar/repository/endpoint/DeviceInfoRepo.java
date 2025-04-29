package com.xii.pillar.repository.endpoint;

import com.mongodb.client.result.UpdateResult;
import com.xii.pillar.domain.endpoint.DeviceInfo;
import com.xii.pillar.repository.BasicRepo;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import static com.xii.pillar.domain.constant.GlobalConstant.OFFLINE;
import static com.xii.pillar.domain.constant.GlobalConstant.ONLINE;

@Component
public class DeviceInfoRepo extends BasicRepo {

    public boolean updateInfo(String deviceId, long lastPublishTime) {
        UpdateResult updateResult = mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(deviceId)),
                Update.update("lastReportTime", System.currentTimeMillis())
                        .set("status", ONLINE)
                        .set("lastPublishTime", lastPublishTime), DeviceInfo.class);

        return updateResult.getModifiedCount() > 0;
    }

    public boolean updateStatus(String deviceId) {
        DeviceInfo deviceInfo = mongoTemplate.findAndModify(Query.query(Criteria.where("_id").is(deviceId).and("status").is(ONLINE)),
                Update.update("status", OFFLINE),
                FindAndModifyOptions.options().returnNew(true), DeviceInfo.class);
        return deviceId != null;
    }
}
