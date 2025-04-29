package com.xii.pillar.service.endpoint;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.googlecode.aviator.AviatorEvaluator;
import com.xii.pillar.connect.ConnectorType;
import com.xii.pillar.connect.SessionHolder;
import com.xii.pillar.domain.endpoint.DeviceInfo;
import com.xii.pillar.domain.snapshot.PTaskSnapshot;
import com.xii.pillar.repository.endpoint.DeviceInfoRepo;
import com.xii.pillar.schema.EndpointMessage;
import com.xii.pillar.schema.PContext;
import com.xii.pillar.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

import static com.xii.pillar.domain.constant.GlobalConstant.OFFLINE;

@Service
@Slf4j
public class DeviceKeeper {

    @Autowired
    private DeviceInfoRepo deviceInfoRepo;

    public void handelStatusReport(EndpointMessage message) {
        boolean isUpdate = deviceInfoRepo.updateInfo(message.getId(), Long.parseLong(message.getPublishTime()));
        if (isUpdate) {
            log.info("# status_update_success. deviceId: {}", message.getId());
        }
    }

    public boolean push(PContext context, PTaskSnapshot taskSnapshot){
        DeviceInfo deviceInfo = deviceInfoRepo.getById(taskSnapshot.getDeviceId(), DeviceInfo.class);
        if (OFFLINE.equals(deviceInfo.getStatus())) {
            log.info("# DEVICE_OFFLINE. deviceId:{}", taskSnapshot.getDeviceId());
            return false;
        }
        ObjectNode message = JsonUtil.createObjectNode();
        try {
            message.put("id", taskSnapshot.getId());
            message.put("name", taskSnapshot.getName());
            HashMap<String, String> params = taskSnapshot.getParams();
            taskSnapshot.getContextParser().forEach((key, parser) -> {
                params.put(key, (String) AviatorEvaluator.execute(parser, context.getSessionMap()));
            });
            message.put("params", JsonUtil.write(params));
        } catch (Exception e) {
            log.error("# BUILD_message_FAIL. taskSnapshot:{}", taskSnapshot, e);
            return false;
        }

        boolean isOK = SessionHolder.sendTextMessage(ConnectorType.ENDPOINT, taskSnapshot.getDeviceId(), message);
        if (!isOK) {
            isOK = deviceInfoRepo.updateStatus(taskSnapshot.getDeviceId());
            log.info("# PUSH_FAIL. deviceId:{}, update device status {}", taskSnapshot.getDeviceId(), isOK);
            return false;
        }

        return true;
    }
}
