package com.xii.pillar.domain.snapshot;

import com.xii.pillar.domain.constant.BaseState;
import com.xii.pillar.domain.constant.ErrorOption;
import com.xii.pillar.domain.constant.TaskType;
import com.xii.pillar.domain.workflow.PTask;
import com.xii.pillar.utils.IdGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.HashMap;

@Document("p_task_snapshot")
public class PTaskSnapshot implements Serializable {
    private String id;
    private String taskId;
    private String executionPathId;
    private String name;
    private HashMap<String, String> contextParser = new HashMap<>();
    private HashMap<String, String> params = new HashMap<>();
    private ErrorOption errorOption;
    private TaskType taskType;
    private Long createAt;
    private BaseState state;

    // task defined
    private String deviceId;
    private String url;
    private String callbackUrl;

    // retry
    private Long expireAt;
    private Integer remainNum;

    // result defined
    private String returnCode;
    private String message;
    private Long startAt;
    private Long endAt;

    public String getId() {
        return id;
    }

    public PTaskSnapshot setId(String id) {
        this.id = id;
        return this;
    }

    public String getTaskId() {
        return taskId;
    }

    public PTaskSnapshot setTaskId(String taskId) {
        this.taskId = taskId;
        return this;
    }

    public String getExecutionPathId() {
        return executionPathId;
    }

    public PTaskSnapshot setExecutionPathId(String executionPathId) {
        this.executionPathId = executionPathId;
        return this;
    }

    public String getName() {
        return name;
    }

    public PTaskSnapshot setName(String name) {
        this.name = name;
        return this;
    }

    public HashMap<String, String> getParams() {
        return params;
    }

    public PTaskSnapshot setParams(HashMap<String, String> params) {
        this.params = params;
        return this;
    }

    public ErrorOption getErrorOption() {
        return errorOption;
    }

    public PTaskSnapshot setErrorOption(ErrorOption errorOption) {
        this.errorOption = errorOption;
        return this;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public PTaskSnapshot setTaskType(TaskType taskType) {
        this.taskType = taskType;
        return this;
    }

    public HashMap<String, String> getContextParser() {
        return contextParser;
    }

    public PTaskSnapshot setContextParser(HashMap<String, String> contextParser) {
        this.contextParser = contextParser;
        return this;
    }

    public Long getCreateAt() {
        return createAt;
    }

    public PTaskSnapshot setCreateAt(Long createAt) {
        this.createAt = createAt;
        return this;
    }

    public BaseState getState() {
        return state;
    }

    public PTaskSnapshot setState(BaseState state) {
        this.state = state;
        return this;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public PTaskSnapshot setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public PTaskSnapshot setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public PTaskSnapshot setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
        return this;
    }

    public Long getExpireAt() {
        return expireAt;
    }

    public PTaskSnapshot setExpireAt(Long expireAt) {
        this.expireAt = expireAt;
        return this;
    }

    public Integer getRemainNum() {
        return remainNum;
    }

    public PTaskSnapshot setRemainNum(Integer remainNum) {
        this.remainNum = remainNum;
        return this;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public PTaskSnapshot setReturnCode(String returnCode) {
        this.returnCode = returnCode;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public PTaskSnapshot setMessage(String message) {
        this.message = message;
        return this;
    }

    public Long getStartAt() {
        return startAt;
    }

    public PTaskSnapshot setStartAt(Long startAt) {
        this.startAt = startAt;
        return this;
    }

    public Long getEndAt() {
        return endAt;
    }

    public PTaskSnapshot setEndAt(Long endAt) {
        this.endAt = endAt;
        return this;
    }

    public static PTaskSnapshot transfer(PTask task, String executionPathId) {
        PTaskSnapshot taskSnapshot = new PTaskSnapshot();
        BeanUtils.copyProperties(taskSnapshot, task);
        return taskSnapshot
                .setId(IdGenerator.uuid())
                .setState(BaseState.IN_PROGRESS)
                .setExecutionPathId(executionPathId)
                .setTaskId(task.getId())
                .setExpireAt(task.getExpireTime() == null ? null : System.currentTimeMillis() + task.getExpireTime())
                .setRemainNum(task.getMaxRetryNum())
                .setCreateAt(System.currentTimeMillis());
    }
}



