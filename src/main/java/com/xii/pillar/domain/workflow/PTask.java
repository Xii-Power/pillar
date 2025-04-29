package com.xii.pillar.domain.workflow;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xii.pillar.domain.constant.ErrorOption;
import com.xii.pillar.domain.constant.TaskType;
import com.xii.pillar.utils.IdGenerator;
import com.xii.pillar.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.HashMap;

import static com.xii.pillar.domain.constant.GlobalConstant.MAX_RETRY_NUM;

@Slf4j
@Document("p_task")
public class PTask implements Serializable {
    private String id;
    private String name;
    // context.param
    private HashMap<String, String> contextParser;
    private HashMap<String, String> params;
    private ErrorOption errorOption;
    private TaskType taskType;
    private Long createAt;
    private Long updateAt;

    // task defined
    private String deviceId;
    private String url;
    private String callbackUrl;

    // retry
    private Long expireTime;
    private Integer maxRetryNum;

    public PTask() {
    }

    public String getId() {
        return id;
    }

    public PTask setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public PTask setName(String name) {
        this.name = name;
        return this;
    }

    public HashMap<String, String> getContextParser() {
        return contextParser;
    }

    public PTask setContextParser(HashMap<String, String> contextParser) {
        this.contextParser = contextParser;
        return this;
    }

    public HashMap<String, String> getParams() {
        return params;
    }

    public PTask setParams(HashMap<String, String> params) {
        this.params = params;
        return this;
    }

    public ErrorOption getErrorOption() {
        return errorOption;
    }

    public PTask setErrorOption(ErrorOption errorOption) {
        this.errorOption = errorOption;
        return this;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public PTask setTaskType(TaskType taskType) {
        this.taskType = taskType;
        return this;
    }

    public Long getCreateAt() {
        return createAt;
    }

    public PTask setCreateAt(Long createAt) {
        this.createAt = createAt;
        return this;
    }

    public Long getUpdateAt() {
        return updateAt;
    }

    public PTask setUpdateAt(Long updateAt) {
        this.updateAt = updateAt;
        return this;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public PTask setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public PTask setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public PTask setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
        return this;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public PTask setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
        return this;
    }

    public Integer getMaxRetryNum() {
        return maxRetryNum;
    }

    public PTask setMaxRetryNum(Integer maxRetryNum) {
        this.maxRetryNum = maxRetryNum;
        return this;
    }

    public static PTask parse(ObjectNode objectNode) {
        PTask task = null;
        try {
            task = new PTask()
                    .setId(IdGenerator.uuid())
                    .setName(JsonUtil.getString(objectNode, "name", null))
                    .setErrorOption(objectNode.has("errorOption") ? ErrorOption.valueOf(objectNode.get("errorOption").asText()) : ErrorOption.BREAK)
                    .setTaskType(objectNode.has("taskType") ? TaskType.valueOf(objectNode.get("taskType").asText()) : null)
                    .setParams(objectNode.has("params") ? JsonUtil.read(objectNode.get("params").asText(), HashMap.class) : null)
                    .setContextParser(objectNode.has("contextParser") ? JsonUtil.read(objectNode.get("contextParser").asText(), HashMap.class) : null)
                    .setDeviceId(JsonUtil.getString(objectNode, "deviceId", null))
                    .setCallbackUrl(JsonUtil.getString(objectNode, "callbackUrl", null))
                    .setUrl(JsonUtil.getString(objectNode, "url", null))
                    .setExpireTime(JsonUtil.getLong(objectNode, "expireTime", null))
                    .setMaxRetryNum(JsonUtil.getInt(objectNode, "maxRetryNum", MAX_RETRY_NUM))
                    .setCreateAt(System.currentTimeMillis())
                    .setUpdateAt(System.currentTimeMillis());
        } catch (Exception e) {
            log.error("parse params error. node:{}", objectNode, e);
        }
        return task;
    }
}



