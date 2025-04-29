package com.xii.pillar.schema;

import java.io.Serializable;

public class EndpointMessage implements Serializable {
    public enum ActionType{
        PING, PONG, TASK_REPORT
    }

    private String id;
    private String action;
    private String publishTime;
    private String code;
    private String result;

    public String getId() {
        return id;
    }

    public EndpointMessage setId(String id) {
        this.id = id;
        return this;
    }

    public String getAction() {
        return action;
    }

    public ActionType getActionType() {
        return ActionType.valueOf(action);
    }

    public EndpointMessage setAction(String action) {
        this.action = action;
        return this;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public EndpointMessage setPublishTime(String publishTime) {
        this.publishTime = publishTime;
        return this;
    }

    public String getCode() {
        return code;
    }

    public EndpointMessage setCode(String code) {
        this.code = code;
        return this;
    }

    public String getResult() {
        return result;
    }

    public EndpointMessage setResult(String result) {
        this.result = result;
        return this;
    }
}
