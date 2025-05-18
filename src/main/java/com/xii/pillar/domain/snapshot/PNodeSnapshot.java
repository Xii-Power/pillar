package com.xii.pillar.domain.snapshot;

import com.xii.pillar.domain.constant.BaseState;
import com.xii.pillar.domain.constant.NodeType;
import com.xii.pillar.domain.workflow.PNode;
import com.xii.pillar.utils.IdGenerator;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.xii.pillar.domain.constant.GlobalConstant.PRE_SNAPSHOT;

@Document("p_node_snapshot")
public class PNodeSnapshot implements Serializable {
    private String id;
    private String nodeId;
    private String flowSnapshotId;
    private String sessionId;
    private String name;
    // PENDING, IN_PROGRESS, FINISHED, FAIL
    private BaseState state;
    private ArrayList<String> preNodeSnapshotIds;
    private NodeType nodeType;

    private Long createAt;
    private Long updateAt;

    public PNodeSnapshot() {
    }

    public String getId() {
        return id;
    }

    public PNodeSnapshot setId(String id) {
        this.id = id;
        return this;
    }

    public String getNodeId() {
        return nodeId;
    }

    public PNodeSnapshot setNodeId(String nodeId) {
        this.nodeId = nodeId;
        return this;
    }

    public String getFlowSnapshotId() {
        return flowSnapshotId;
    }

    public PNodeSnapshot setFlowSnapshotId(String flowSnapshotId) {
        this.flowSnapshotId = flowSnapshotId;
        return this;
    }

    public String getSessionId() {
        return sessionId;
    }

    public PNodeSnapshot setSessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    public String getName() {
        return name;
    }

    public PNodeSnapshot setName(String name) {
        this.name = name;
        return this;
    }

    public BaseState getState() {
        return state;
    }

    public PNodeSnapshot setState(BaseState state) {
        this.state = state;
        return this;
    }

    public ArrayList<String> getPreNodeSnapshotIds() {
        return preNodeSnapshotIds;
    }

    public PNodeSnapshot setPreNodeSnapshotIds(ArrayList<String> preNodeSnapshotIds) {
        this.preNodeSnapshotIds = preNodeSnapshotIds;
        return this;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public PNodeSnapshot setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
        return this;
    }

    public Long getCreateAt() {
        return createAt;
    }

    public PNodeSnapshot setCreateAt(Long createAt) {
        this.createAt = createAt;
        return this;
    }

    public Long getUpdateAt() {
        return updateAt;
    }

    public PNodeSnapshot setUpdateAt(Long updateAt) {
        this.updateAt = updateAt;
        return this;
    }

    public static PNodeSnapshot transfer(PNode node, List<String> preNodeSnapshotIds,
                                         String flowSnapshotId, String sessionId) {
        return new PNodeSnapshot()
                .setId(IdGenerator.uuid())
                .setName(PRE_SNAPSHOT + node.getName())
                .setNodeId(node.getId())
                .setFlowSnapshotId(flowSnapshotId)
                .setSessionId(sessionId)
                .setNodeType(node.getNodeType())
                .setPreNodeSnapshotIds((ArrayList<String>) preNodeSnapshotIds)
                .setState(BaseState.PENDING)
                .setCreateAt(System.currentTimeMillis())
                .setUpdateAt(System.currentTimeMillis())
                ;
    }
}