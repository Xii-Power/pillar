package com.xii.pillar.service.endpoint;

import com.xii.pillar.schema.EndpointMessage;
import com.xii.pillar.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MessageReportService {

    @Autowired
    private DeviceKeeper deviceKeeper;

    public void handle(String deviceId, String text) {
        try {
            EndpointMessage message = JsonUtil.read(text, EndpointMessage.class);
            switch (message.getActionType()) {
                case PING:
                case PONG:
                    deviceKeeper.handelStatusReport(message);
                    break;
                case TASK_REPORT:

                    break;
            }

        } catch (Exception e) {
            log.error("# handle_message_error. deviceId:{}", deviceId, e);
        }
    }





}
