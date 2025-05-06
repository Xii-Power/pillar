package com.xii.pillar.domain.snapshot;

import com.xii.pillar.domain.constant.BaseState;
import com.xii.pillar.domain.workflow.PFlow;
import com.xii.pillar.utils.IdGenerator;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

import static com.xii.pillar.domain.constant.GlobalConstant.PRE_SNAPSHOT;
import static com.xii.pillar.domain.constant.GlobalConstant.SCAN_MODE_IDLE;

@Document("p_flow_snapshot")
public class PFlowSnapshot implements Serializable {
    private String id;
    private String flowId;
    private String sessionId;
    private String name;
    private String groupId;
    // PENDING, IN_PROGRESS,  FINISHED, FAIL
    private BaseState state;
    private Integer priority;
    private String scanMode;

    private Long createAt;
    private Long updateAt;

    public PFlowSnapshot() {
    }

    public String getId() {
        return id;
    }

    public PFlowSnapshot setId(String id) {
        this.id = id;
        return this;
    }

    public String getFlowId() {
        return flowId;
    }

    public PFlowSnapshot setFlowId(String flowId) {
        this.flowId = flowId;
        return this;
    }

    public String getSessionId() {
        return sessionId;
    }

    public PFlowSnapshot setSessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    public String getName() {
        return name;
    }

    public PFlowSnapshot setName(String name) {
        this.name = name;
        return this;
    }

    public String getGroupId() {
        return groupId;
    }

    public PFlowSnapshot setGroupId(String groupId) {
        this.groupId = groupId;
        return this;
    }

    public BaseState getState() {
        return state;
    }

    public PFlowSnapshot setState(BaseState state) {
        this.state = state;
        return this;
    }

    public Integer getPriority() {
        return priority;
    }

    public PFlowSnapshot setPriority(Integer priority) {
        this.priority = priority;
        return this;
    }

    public String getScanMode() {
        return scanMode;
    }

    public PFlowSnapshot setScanMode(String scanMode) {
        this.scanMode = scanMode;
        return this;
    }

    public Long getCreateAt() {
        return createAt;
    }

    public PFlowSnapshot setCreateAt(Long createAt) {
        this.createAt = createAt;
        return this;
    }

    public Long getUpdateAt() {
        return updateAt;
    }

    public PFlowSnapshot setUpdateAt(Long updateAt) {
        this.updateAt = updateAt;
        return this;
    }

    public static PFlowSnapshot transfer(PFlow flow, String sessionId) {
        return new PFlowSnapshot()
                .setId(IdGenerator.uuid())
                .setSessionId(sessionId)
                .setFlowId(flow.getId())
                .setName(PRE_SNAPSHOT + flow.getName())
                .setGroupId(flow.getGroupId())
                .setPriority(flow.getPriority())
                .setScanMode(SCAN_MODE_IDLE)
                .setState(BaseState.PENDING)
                .setCreateAt(System.currentTimeMillis())
                .setUpdateAt(System.currentTimeMillis());

    }
}
