package com.xii.pillar.domain.endpoint;

import com.xii.pillar.utils.IdGenerator;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

import static com.xii.pillar.domain.constant.GlobalConstant.OFFLINE;

@Document("device_info")
public class DeviceInfo implements Serializable {


    /**
     * deviceId
     */
    private String id;
    private String name;
    private String status;
    private Long lastPublishTime;
    private Long createTime;
    private Long lastReportTime;

    public DeviceInfo() {
    }

    public DeviceInfo(String name) {
        this.id = IdGenerator.uuid();
        this.name = name;
        this.status = OFFLINE;
        this.createTime = System.currentTimeMillis();
        this.lastPublishTime = this.createTime;
        this.lastReportTime = this.createTime;
    }

    public String getId() {
        return id;
    }

    public DeviceInfo setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public DeviceInfo setName(String name) {
        this.name = name;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public DeviceInfo setStatus(String status) {
        this.status = status;
        return this;
    }

    public Long getLastPublishTime() {
        return lastPublishTime;
    }

    public DeviceInfo setLastPublishTime(Long lastPublishTime) {
        this.lastPublishTime = lastPublishTime;
        return this;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public DeviceInfo setCreateTime(Long createTime) {
        this.createTime = createTime;
        return this;
    }

    public Long getLastReportTime() {
        return lastReportTime;
    }

    public DeviceInfo setLastReportTime(Long lastReportTime) {
        this.lastReportTime = lastReportTime;
        return this;
    }
}
