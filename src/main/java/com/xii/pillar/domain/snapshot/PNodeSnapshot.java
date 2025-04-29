package com.xii.pillar.domain.snapshot;

import com.xii.pillar.domain.constant.BaseState;
import com.xii.pillar.domain.constant.NodeType;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.ArrayList;

@Document("p_node_snapshot")
public class PNodeSnapshot implements Serializable {
    private String id;
    private String nodeId;
    private String flowSnapshotId;
    private String name;
    private BaseState state;
    private ArrayList<String> preNodeSnapshotIds;
    private NodeType nodeType;

    private Long createAt;
    private Long updateAt;

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
}